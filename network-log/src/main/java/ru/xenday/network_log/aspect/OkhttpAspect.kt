package ru.xenday.network_log.aspect

import okhttp3.Response
import okhttp3.internal.connection.RealCall
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
 * This aspect intercepts the execution of OkHttp requests made through the `Call.execute()` method
 * and provides monitoring of the request and response.
 *
 * @see BaseAspect
 */
@Aspect
class OkhttpAspect : BaseAspect() {

    /**
     * This method intercepts the execution of OkHttp requests made through the `Call.execute()` method.
     * It monitors the request and response and notifies the HTTP listener.
     *
     * @param joinPoint The join point at which the execution of the intercepted method occurs.
     * @return The response returned by the intercepted method.
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* okhttp3.Call+.execute(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint): Any? {
        val call = joinPoint.target as RealCall
        val okRequest = call.originalRequest

        val codeSource = joinPoint.toCodeSource()
        val bytes = (okRequest.body?.contentLength() ?: 0) + okRequest.headers.byteCount()
        val request = JVMRequest(
            codeSource = codeSource,
            bytes = bytes,
            url = okRequest.url.toString(),
        )

        if (!requestFilter.filter(request)) {
            return joinPoint.run()
        }

        val (res, time) = joinPoint.runWithTime()

        val okResponse = res as Response
        val resBytes = (okResponse.body?.contentLength() ?: 0) + okResponse.headers.byteCount()
        val response = JVMResponse(
            bytes = resBytes,
            status = okResponse.code,
            duration = time,
        )

        if (!responseFilter.filter(response)) {
            return joinPoint.run()
        }

        httpListener.onLibrarySend(request, response)

        return res
    }
}