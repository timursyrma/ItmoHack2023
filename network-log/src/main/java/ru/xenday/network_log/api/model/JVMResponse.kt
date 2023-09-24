/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.model

import kotlin.time.Duration

/**
 * Represents a response from a JVM request.
 */
data class JVMResponse(
    /**
     * The number of bytes in the response.
     */
    val bytes: Long?,
    /**
     * The HTTP status code of the response.
     */
    val status: Int?,
    /**
     * The duration of the request/response cycle.
     */
    val duration: Duration,
)