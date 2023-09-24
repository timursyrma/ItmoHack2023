package ru.xenday.network_log.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Pointcut
import ru.xenday.network_log.api.HttpListener
import ru.xenday.network_log.api.RequestFilter
import ru.xenday.network_log.api.ResponseFilter
import ru.xenday.network_log.utils.PropertyReader

/**
 * The BaseAspect class is an abstract AspectJ aspect that serves as the base class for other aspects in the ru.xenday.network_log.aspect package.
 * It provides common functionality and properties for these aspects, such as request and response filters and the HTTP listener.
 * @version 1.0
 * @since 2023-04-16
 * @property requestFilter the RequestFilter object used to filter HTTP requests.
 * @property responseFilter the ResponseFilter object used to filter HTTP responses.
 * @property httpListener the HttpListener object used to handle HTTP requests and responses.
 */
abstract class BaseAspect {
    protected val requestFilter: RequestFilter = PropertyReader.requestFilter
    protected val responseFilter: ResponseFilter = PropertyReader.responseFilter
    protected val httpListener: HttpListener = PropertyReader.httpListener

    /**
     * This abstract method is called before a join point execution.
     * It is implemented by subclasses to provide specific functionality for each aspect.
     *
     * @param joinPoint the `ProceedingJoinPoint` object representing the intercepted method call.
     * @return the result of the intercepted method call, if any.
     */
    abstract fun around(joinPoint: ProceedingJoinPoint): Any?

    /**
     * This pointcut expression matches any join point that does not have the `Untrackable` annotation.
     */
    @Pointcut("!@this(ru.xenday.network_log.annotation.Untrackable)")
    protected fun untrackable() = Unit
}