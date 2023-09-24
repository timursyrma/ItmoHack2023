/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api.model

import java.time.Instant

/**
 * A class that represents a potential external request.
 * @param codeSource the code source of the potential external request.
 * @param startAt the time the potential external request started.
 */
data class PotentialExternalRequest(
    val codeSource: CodeSource,
    val startAt: Instant = Instant.now(),
)