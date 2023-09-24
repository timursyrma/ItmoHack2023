package ru.xenday.network_log.aspect

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
 * This aspect intercepts the method call to com.google.android.exoplayer2.Player.prepare() method and logs the network request and response.
 * If the request or response is filtered out by the filters, the aspect does not log anything.
 * @property requestFilter a filter for network requests. If a request is filtered out, it will not be logged.
 * @property responseFilter a filter for network responses. If a response is filtered out, it will not be logged.
 * @property httpListener a listener for the network logs. It receives both the request and response objects.
 */
@Aspect
class ExoPlayerAspect : BaseAspect() {

    /**
     * Intercepts the com.google.android.exoplayer2.Player.prepare() method call and logs the network request and response.
     * @param joinPoint the ProceedingJoinPoint object that represents the method call.
     */
    @OptIn(ExperimentalTime::class)
    @Around("call(* com.google.android.exoplayer2.Player+.prepare(..)) && untrackable()")
    override fun around(joinPoint: ProceedingJoinPoint) {
        val codeSource = joinPoint.toCodeSource()
        val request = JVMRequest(
            codeSource = codeSource,
            bytes = null,
            url = null,
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