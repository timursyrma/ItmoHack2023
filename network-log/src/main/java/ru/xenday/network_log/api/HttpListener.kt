/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api

import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.api.model.PotentialExternalRequest

/**
 * An interface that provides the ability to track application traffic.
 */
interface HttpListener {
    /**
     * Called when an HTTP request is sent by an external library.
     * @param request the JVMRequest object representing the request.
     * @param response the JVMResponse object representing the response.
     */
    fun onLibrarySend(request: JVMRequest, response: JVMResponse)

    /**
     * Called when an external call is made.
     * @param request the PotentialExternalRequest object representing the request.
     */
    fun onExternalCall(request: PotentialExternalRequest)

    /**
     * Called when an external tick occurs.
     * @param tick the Tick object representing the tick.
     */
    fun onExternalTick(tick: Tick)
}