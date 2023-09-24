/**
 * The Utils package provides utility functions used by the network logging library.
 */
package ru.xenday.network_log.utils

import ru.xenday.network_log.api.impl.DefaultHttpListener
import ru.xenday.network_log.api.impl.DefaultRequestFilter
import ru.xenday.network_log.api.impl.DefaultResponseFilter
import ru.xenday.network_log.api.HttpListener
import ru.xenday.network_log.api.RequestFilter
import ru.xenday.network_log.api.ResponseFilter
import java.util.Properties

/**
 * The PropertyReader object is a utility class that reads the configuration properties needed for the network logging library.
 * It reads the values from a config.properties file located in the resources folder and provides the default implementations for the RequestFilter, ResponseFilter, and HttpListener interfaces in case the values are not specified.
 */
object PropertyReader {
    /**
     * The properties object contains the key-value pairs read from the config.properties file.
     */
    private val properties: Properties by lazy {
        Properties().apply {
            val inputStream = {}::class.java.classLoader
                ?.getResource("config.properties")
                ?.openStream()

            load(inputStream)
        }
    }

    /**
     * The requestFilter property provides an instance of the RequestFilter interface.
     * If the network.log.request_filter property is specified in the config.properties file, it creates an instance of the class specified by the property value.
     * Otherwise, it provides the default implementation, DefaultRequestFilter.
     * @return a RequestFilter instance
     */
    val requestFilter: RequestFilter by lazy {
        val filter = properties["network.log.request_filter"] as String?

        filter?.let { Class.forName(it).newInstance() as RequestFilter } ?: DefaultRequestFilter()
    }

    /**
     * The responseFilter property provides an instance of the ResponseFilter interface.
     * If the network.log.response_filter property is specified in the config.properties file, it creates an instance of the class specified by the property value.
     * Otherwise, it provides the default implementation, DefaultResponseFilter.
     * @return a ResponseFilter instance
     */
    val responseFilter: ResponseFilter by lazy {
        val filter = properties["network.log.response_filter"] as String?

        filter?.let { Class.forName(it).newInstance() as ResponseFilter } ?: DefaultResponseFilter()
    }

    /**
     * The httpListener property provides an instance of the HttpListener interface.
     * If the network.log.http_listener property is specified in the config.properties file, it creates an instance of the class specified by the property value.
     * Otherwise, it provides the default implementation, DefaultHttpListener.
     * @return an HttpListener instance
     */
    val httpListener: HttpListener by lazy {
        val listener = properties["network.log.http_listener"] as String?

        listener?.let { Class.forName(it).newInstance() as HttpListener } ?: DefaultHttpListener()
    }
}