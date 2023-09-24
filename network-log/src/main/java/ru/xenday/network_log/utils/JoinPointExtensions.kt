/**
 * The Utils package provides utility functions used by the network logging library.
 */
package ru.xenday.network_log.utils

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import ru.xenday.network_log.api.model.CodeSource
import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

/**
 * Runs the intercepted method call represented by the current ProceedingJoinPoint and returns its result.
 * @receiver the ProceedingJoinPoint representing the intercepted method call
 * @return the result of the intercepted method call
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun ProceedingJoinPoint.run(): Any? = proceed(args)

/**
 * Converts the current JoinPoint to a CodeSource object.
 * @receiver the JoinPoint to convert
 * @return the CodeSource object representing the JoinPoint
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun JoinPoint.toCodeSource(): CodeSource {
    val packages = sourceLocation.withinType.name.split(".", limit = 4)
    val prefix = packages.subList(0, minOf(packages.size - 1, 3)).joinToString(separator = ".")
    val stackTrace = Thread.currentThread().stackTrace

    val hashCode = stackTrace.asSequence().drop(3)
        .map { it.className }
        .takeWhile { it.startsWith(prefix) }
        .fold(0L) { acc, s ->
            acc * 31 + s.hashCode()
        }

    return CodeSource(
        id = hashCode,
        className = sourceLocation.fileName,
        line = sourceLocation.line,
    )
}

/**
 * Runs the intercepted method call represented by the current ProceedingJoinPoint, measures its execution time, and returns both the result and the execution time.
 * @receiver the ProceedingJoinPoint representing the intercepted method call
 * @return a TimedValue object containing the result of the intercepted method call and its execution time
 */
@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalTime::class)
internal inline fun ProceedingJoinPoint.runWithTime(): TimedValue<Any?> = measureTimedValue {
    run()
}