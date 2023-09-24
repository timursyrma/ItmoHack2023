/**
 * A functional interface that defines a request filter.
 */
package ru.xenday.network_log.api

import ru.xenday.network_log.api.model.JVMRequest

/**
 * Filters a JVM request based on some criteria.
 *
 * @param request the JVM request to be filtered.
 * @return true if the request passes the filter, false otherwise.
 */
fun interface RequestFilter {
    fun filter(request: JVMRequest): Boolean
}