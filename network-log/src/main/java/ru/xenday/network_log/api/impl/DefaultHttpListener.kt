/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.impl

import ru.xenday.network_log.api.HttpListener
import ru.xenday.network_log.api.NetworkStatisticsScheduler
import ru.xenday.network_log.api.Tick
import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.api.model.PotentialExternalRequest

/**
 * A default implementation of the HttpListener interface.
 */
class DefaultHttpListener : HttpListener {

    /**
     * Constructs a new DefaultHttpListener object and starts a NetworkStatisticsScheduler to periodically collect network statistics.
     */
    init {
        NetworkStatisticsScheduler(this)
    }

    /**
     * Called when an HTTP request is sent by an external library.
     * @param request the JVMRequest object representing the request.
     * @param response the JVMResponse object representing the response.
     */
    override fun onLibrarySend(request: JVMRequest, response: JVMResponse) {
        println("request: $request, response: $response")
    }

    /**
     * Called when an external call is made.
     * @param request the PotentialExternalRequest object representing the request.
     */
    override fun onExternalCall(request: PotentialExternalRequest) {
        // Do nothing
    }

    /**
     * Called when an external tick occurs.
     * @param tick the Tick object representing the tick.
     */
    override fun onExternalTick(tick: Tick) {
        // Do nothing
    }
}