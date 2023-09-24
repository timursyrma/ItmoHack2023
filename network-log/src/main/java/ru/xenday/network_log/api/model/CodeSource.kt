/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.model

/**
 * Represents the source code information for a JVM request.
 */
data class CodeSource(
    /**
     * The unique id of code source
     */
    val id: Long,
    /**
     * The name of the class containing the code source
     */
    val className: String,

    /**
     * The line number of the code source
     */
    val line: Int,
)