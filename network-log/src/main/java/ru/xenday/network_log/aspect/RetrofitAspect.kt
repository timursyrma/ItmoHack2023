package ru.xenday.network_log.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import retrofit2.Call
import retrofit2.Response
import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.utils.run
import ru.xenday.network_log.utils.runWithTime
import ru.xenday.network_log.utils.toCodeSource
import java.time.Instant
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * Aspect that intercepts Retrofit HTTP requests and responses, and logs information about them using a HttpListener.
 * This class intercepts calls to execute() and onResponse() methods of Retrofit Call and Callback classes respectively, and logs the request and response information to a HttpListener if they pass through the filters.
 */
@Aspect
class RetrofitAspect : BaseAspect() {

    /**
     * Intercepts the execute() method of a Retrofit Call instance, and logs the request and response information to a HttpListener.
     * This method intercepts the execute() method of a Retrofit Call instance, and constructs a
     * JVMRequest object from the request data, and a JVMResponse object from the response data. It then passes these objects to a HttpListener, if they pass through the filters.
     * @param joinPoint the ProceedingJoinPoint instance representing the method call to be intercepted.
     * @return the result of the intercepted method call.
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* retrofit2.Call+.execute(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint): Any? {
        val call = joinPoint.target as Call<*>
        val retroRequest = call.request()

        val codeSource = joinPoint.toCodeSource()
        val bytes = (retroRequest.body?.contentLength() ?: 0) + retroRequest.headers.byteCount()
        val request = JVMRequest(
            codeSource = codeSource,
            bytes = bytes,
            url = retroRequest.url.toString(),
        )

        if (!requestFilter.filter(request)) {
            return joinPoint.run()
        }

        val (res, time) = joinPoint.runWithTime()

        val retroResponse = res as Response<*>
        val resBytes =
            (retroResponse.raw().body?.contentLength() ?: 0) + retroResponse.headers().byteCount()
        val response = JVMResponse(
            bytes = resBytes,
            status = retroResponse.code(),
            duration = time,
        )

        if (!responseFilter.filter(response)) {
            return joinPoint.run()
        }

        httpListener.onLibrarySend(request, response)

        return res
    }

    /**
     * Intercepts the onResponse() method of a Retrofit Callback instance, and logs the request and response
     * information to a HttpListener.
     * This method intercepts the onResponse() method of a Retrofit Callback instance, and constructs a
     */
    @Before("call(* retrofit2.Callback+.onResponse(..)) && untrackable()")
    fun aroundCallback(joinPoint: JoinPoint) {
        val (callRaw, resRaw) = joinPoint.args

        val call = callRaw as Call<*>
        val res = resRaw as Response<*>
        val req = call.request()

        val codeSource = joinPoint.toCodeSource()
        val bytes = (req.body?.contentLength() ?: 0) + req.headers.byteCount()
        val time = res.raw().sentRequestAtMillis + res.raw().receivedResponseAtMillis

        val request = JVMRequest(
            codeSource = codeSource,
            bytes = bytes,
            url = req.url.toString(),
            startAt = Instant.now().minusMillis(time),
        )

        if (!requestFilter.filter(request)) {
            return
        }

        val resBytes = (res.raw().body?.contentLength() ?: 0) + res.headers().byteCount()
        val response = JVMResponse(
            bytes = resBytes,
            status = res.code(),
            duration = time.toDuration(DurationUnit.MILLISECONDS),
        )

        if (!responseFilter.filter(response)) {
            return
        }

        httpListener.onLibrarySend(request, response)
    }
}