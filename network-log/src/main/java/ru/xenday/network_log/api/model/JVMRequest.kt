/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.model

import java.time.Instant

/**
 * Represents a JVM request with information about the code source, bytes, URL, and start time.
 */
data class JVMRequest(
    /**
     * The code source of the request.
     */
    val codeSource: CodeSource,

    /**
     * The number of bytes in the request, or null if not available.
     */
    val bytes: Long?,

    /**
     * The URL associated with the request, or null if not available.
     */
    val url: String?,

    /**
     * The start time of the request.
     */
    val startAt: Instant = Instant.now(),
)
