/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.impl

import ru.xenday.network_log.api.ResponseFilter
import ru.xenday.network_log.api.model.JVMResponse

/**
 * Simple implementation of JVM response filter.
 */
class DefaultResponseFilter : ResponseFilter {
    /**
     * Returns true for all JVM responses, indicating that they should not be filtered.
     * @param response the JVMResponse object to be filtered.
     * @return true indicating that the response should not be filtered.
     */
    override fun filter(response: JVMResponse): Boolean = true
}