package ru.xenday.network_log.api

import java.time.Instant

/**
 * A data class representing a network traffic tick, which is the amount of sent and received data within a time interval.
 */
data class Tick(

    /**
     * The amount of data sent during the tick, in bytes.
     */
    val sent: Long,

    /**
     * The amount of data received during the tick, in bytes.
     */
    val received: Long,

    /**
     * The timestamp of the tick, represented as an instance of the Instant class.
     */
    val timestamp: Instant = Instant.now(),
)