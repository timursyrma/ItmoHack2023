package ru.xenday.network_log.aspect.external

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import ru.xenday.network_log.api.model.PotentialExternalRequest
import ru.xenday.network_log.utils.toCodeSource

/**
 * The WebViewAspect class is an AspectJ aspect that intercepts calls to the loadDataWithBaseURL method of the android.webkit.WebView class.
 * It extends the BaseExternalAspect class and overrides its around method to extract information about potential external requests and notify the HTTP listener about them.
 */
@Aspect
class WebViewAspect : BaseExternalAspect() {

    /**
     * This method is called before the `loadDataWithBaseURL` method of the `android.webkit.WebView` class is executed.
     * It overrides the `around` method of the `BaseExternalAspect` class to extract information about potential external requests
     * and notify the HTTP listener about them.
     *
     * @param joinPoint the `JoinPoint` object representing the intercepted method call.
     */
    @Before("call(* android.webkit.WebView.loadDataWithBaseURL(..)) && untrackable()")
    override fun before(joinPoint: JoinPoint) {
        val codeSource = joinPoint.toCodeSource()
        val request = PotentialExternalRequest(
            codeSource = codeSource,
        )

        httpListener.onExternalCall(request)
    }
}