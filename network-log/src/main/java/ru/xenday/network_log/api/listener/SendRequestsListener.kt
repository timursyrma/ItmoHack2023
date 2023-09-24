package ru.xenday.network_log.api.listener

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ru.xenday.network_log.annotation.Untrackable
import ru.xenday.network_log.api.HttpListener
import ru.xenday.network_log.api.Tick
import ru.xenday.network_log.api.model.JVMRequest
import ru.xenday.network_log.api.model.JVMResponse
import ru.xenday.network_log.api.model.PotentialExternalRequest

@Untrackable
class SendRequestsListener : HttpListener {

    private fun sendEvent(type: String, time_point: Long, size: Long?) {
        val event = JSONObject()
            .put("time_point", time_point)
            .put("size", size)
            .put("type", type)

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val request: Request = Request.Builder()
            .url("http://127.0.0.1:8080")
            .post(event.toString().toRequestBody(mediaType))
            .build()

        OkHttpClient().newCall(request).execute()
    }

    override fun onLibrarySend(request: JVMRequest, response: JVMResponse) {
        sendEvent("jvm", request.startAt.toEpochMilli(), request.bytes)
    }

    override fun onExternalCall(request: PotentialExternalRequest) {
        sendEvent("potential", request.startAt.toEpochMilli(), null)
    }

    override fun onExternalTick(tick: Tick) {
        sendEvent("tick", tick.timestamp.toEpochMilli(), tick.sent)
    }

}