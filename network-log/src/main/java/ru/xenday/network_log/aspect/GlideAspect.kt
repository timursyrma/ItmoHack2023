package ru.xenday.network_log.aspect

import com.bumptech.glide.RequestBuilder
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
 * An aspect that intercepts calls to the Glide library's `into()` method and logs the resulting network request
 * and response using the provided `HttpListener`.
 *
 * @see BaseAspect
 */
@Aspect
class GlideAspect : BaseAspect() {

    /**
     * Intercepts calls to the `into()` method of a `RequestBuilder` instance and logs the network request and
     * response using the provided `HttpListener`.
     *
     * @param joinPoint the `ProceedingJoinPoint` representing the intercepted method call
     * @return the result of the intercepted method call
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* com.bumptech.glide.RequestBuilder.into(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint): Any? {
        val glideRequest = joinPoint.target as RequestBuilder<*>

        val codeSource = joinPoint.toCodeSource()

        val request = JVMRequest(
            codeSource = codeSource,
            bytes = null,
            url = glideRequest.url,
        )

        if (!requestFilter.filter(request)) {
            joinPoint.run()
        }

        val (req, time) = joinPoint.runWithTime()

        val response = JVMResponse(
            bytes = null,
            status = null,
            duration = time,
        )

        if (!responseFilter.filter(response)) {
            joinPoint.run()
        }

        httpListener.onLibrarySend(request, response)

        return req
    }

    /**
     * Provides reflection access to the `url` field of a `RequestBuilder` instance.
     */
    private companion object {
        val field: Field = RequestBuilder::class.java.getDeclaredField("model").apply {
            isAccessible = true
        }

        /**
         * Returns the value of the `url` field of a `RequestBuilder` instance.
         * @return the value of the `url` field
         */
        val RequestBuilder<*>.url: String
            get() = field.get(this) as String
    }
}