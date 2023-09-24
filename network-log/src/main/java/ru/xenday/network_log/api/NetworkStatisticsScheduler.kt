/**
 * This package contains classes and interfaces for the network log API.
 */
package ru.xenday.network_log.api

import android.net.TrafficStats
import android.os.Process
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A class that schedules network statistics to be recorded periodically and reported to an HttpListener.
 * @param httpListener the HttpListener to report the network statistics to.
 */
class NetworkStatisticsScheduler(private val httpListener: HttpListener) {
    /**
     * The UID of the current process.
     */
    private val uid: Int = Process.myUid()

    /**
     * Constructs a new NetworkStatisticsScheduler object and starts the scheduler.
     */
    init {
        runScheduler()
    }

    /**
     * Runs the network statistics scheduler.
     */
    private fun runScheduler() {
        CoroutineScope(Dispatchers.Default).launch {
            do {
                val (prevSent, prevReceived) = getCurrentDataUsage()
                delay(1_000)
                val (curSent, curReceived) = getCurrentDataUsage()
                httpListener.onExternalTick(
                    Tick(
                        sent = curSent - prevSent,
                        received = curReceived - prevReceived,
                    )
                )
            } while (true)
        }
    }

    /**
     * Returns the current data usage for the current process.
     * @return a pair containing the number of bytes sent and received by the current process.
     */
    private fun getCurrentDataUsage() =
        TrafficStats.getUidTxBytes(uid) to TrafficStats.getUidRxBytes(uid)
}