package ru.xenday.network_log.aspect.external

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import ru.xenday.network_log.aspect.BaseAspect

/**
 * Base aspect for intercepting external library calls.
 */
abstract class BaseExternalAspect : BaseAspect() {
    /**
     * Intercepts the given join point.
     * @param joinPoint the join point to intercept.
     */
    override fun around(joinPoint: ProceedingJoinPoint): Any = before(joinPoint as JoinPoint)

    abstract fun before(joinPoint: JoinPoint)
}