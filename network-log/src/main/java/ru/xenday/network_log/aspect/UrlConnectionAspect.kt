package ru.xenday.network_log.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.utils.run
import ru.xenday.network_log.utils.toCodeSource
import java.net.URLConnection
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * The UrlConnectionAspect class is an aspect that intercepts calls to the getInputStream() method of the java.net.URLConnection class,
 * and logs information about the request and response made by the connection.
 * @author [author's name]
 * @version 1.0
 * @since [date of creation]
 */
@Aspect
class UrlConnectionAspect : BaseAspect() {

    /**
     * Intercepts the call to the `getInputStream()` method of the `java.net.URLConnection` class, and logs information about the request and response made by the connection.
     *
     * @param joinPoint the ProceedingJoinPoint that represents the intercepted method call
     * @return the result of the intercepted method call
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* java.net.URLConnection+.getInputStream(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint): Any? {
        val connection = joinPoint.target as URLConnection

        val codeSource = joinPoint.toCodeSource()
        val bytes = connection.contentLength.toLong()
        val request = JVMRequest(
            codeSource = codeSource,
            bytes = bytes,
            url = connection.url.toString(),
        )

        if (!requestFilter.filter(request)) {
            return joinPoint.run()
        }

        val (res, time) = measureTimedValue {
            joinPoint.run()
        }

        val response = JVMResponse(
            bytes = null,
            status = null,
            duration = time,
        )

        if (!responseFilter.filter(response)) {
            return joinPoint.run()
        }

        httpListener.onLibrarySend(request, response)

        return res
    }


}