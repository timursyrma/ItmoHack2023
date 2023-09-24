/**
 * A functional interface that defines a response filter.
 */
package ru.xenday.network_log.api

import ru.xenday.network_log.api.model.JVMResponse

/**
 * Filters a JVM response based on some criteria.
 *
 * @param response the JVM response to be filtered.
 * @return true if the response passes the filter, false otherwise.
 */
fun interface ResponseFilter {
    fun filter(response: JVMResponse): Boolean
}