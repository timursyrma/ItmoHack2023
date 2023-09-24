package ru.xenday.network_log.aspect

import com.squareup.picasso.Request
import com.squareup.picasso.RequestCreator
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.utils.run
import ru.xenday.network_log.utils.runWithTime
import ru.xenday.network_log.utils.toCodeSource
import java.lang.reflect.Field
import kotlin.time.ExperimentalTime

/**
 * Aspect for intercepting and logging network requests made with Picasso library.
 * @constructor Creates a new instance of PicassoAspect
 */
@Aspect
class PicassoAspect : BaseAspect() {

    /**
     * Intercepts and logs the network requests made with Picasso.
     * @param joinPoint the join point of the intercepted method call.
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* com.squareup.picasso.RequestCreator.into(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint) {
        val req = joinPoint.target as RequestCreator
        val picassoRequest = req.request

        val codeSource = joinPoint.toCodeSource()
        val request = JVMRequest(
            codeSource = codeSource,
            bytes = null,
            url = picassoRequest.uri.toString(),
        )

        if (!requestFilter.filter(request)) {
            joinPoint.run()
        }

        val (_, time) = joinPoint.runWithTime()

        val response = JVMResponse(
            bytes = null,
            status = null,
            duration = time,
        )

        if (!responseFilter.filter(response)) {
            joinPoint.run()
        }

        httpListener.onLibrarySend(request, response)
    }

    private companion object {
        val dataField: Field = RequestCreator::class.java.getDeclaredField("data").apply {
            isAccessible = true
        }

        /**
         * Gets the request associated with this RequestCreator instance.
         * @return the request.
         */
        val RequestCreator.request: Request
            get() = (dataField.get(this) as Request.Builder).build()
    }
}