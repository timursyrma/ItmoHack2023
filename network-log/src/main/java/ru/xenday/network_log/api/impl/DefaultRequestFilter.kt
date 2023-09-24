/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.impl

import ru.xenday.network_log.api.RequestFilter
import ru.xenday.network_log.api.model.JVMRequest

/**
 * A simple implementation of the RequestFilter interface that allows all requests.
 */
class DefaultRequestFilter : RequestFilter {
    /**
     * Returns true for all JVM requests, indicating that they should not be filtered.
     * @param request the JVMRequest object to be filtered.
     * @return true indicating that the request should not be filtered.
     */
    override fun filter(request: JVMRequest): Boolean = true
}