package ru.xenday.network_log.aspect

import com.facebook.imagepipeline.request.ImageRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.utils.run
import ru.xenday.network_log.utils.runWithTime
import ru.xenday.network_log.utils.toCodeSource
import kotlin.time.ExperimentalTime

/**
 * An aspect to intercept and log network requests made through Fresco library.
 * Extends the abstract class BaseAspect which implements some common methods and properties.
 * @property requestFilter A filter to determine whether or not to log a request
 * @property responseFilter A filter to determine whether or not to log a response
 * @property httpListener A listener to handle logged requests and responses
 */
@Aspect
class FrescoAspect : BaseAspect() {

    /**
     * An advice to intercept the setImageRequest method of SimpleDraweeView class.
     * Logs the request and response using the httpListener if the request and response pass the filters.
     * @param joinPoint A JoinPoint object containing information about the method being intercepted
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* com.facebook.drawee.view.SimpleDraweeView+.setImageRequest(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint) {
        val imageRequest = joinPoint.args.first() as ImageRequest

        val codeSource = joinPoint.toCodeSource()

        val request = JVMRequest(
            codeSource = codeSource,
            bytes = null,
            url = imageRequest.sourceUri.toString(),
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
}