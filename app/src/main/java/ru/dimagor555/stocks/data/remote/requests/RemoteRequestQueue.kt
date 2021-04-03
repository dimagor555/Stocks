package ru.dimagor555.stocks.data.remote.requests

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRequestQueue @Inject constructor() {
    private val requestQueue = ArrayList<RemoteRequest>(100)

    val countOfRequestInProcess
        get() = requestQueue.count { it.isInProgress }

    @Synchronized
    fun addRequest(request: RemoteRequest) {
        if (!requestQueue.contains(request)) {
            requestQueue.add(request)
            requestQueue.sort()
        }
    }

    @Synchronized
    fun getRequestToExecute(): RemoteRequest? {
        removeFinishedRequests()
        for (request in requestQueue) {
            if (!request.isInProgress) {
                return request
            }
        }
        return null
    }

    @Synchronized
    private fun removeFinishedRequests() {
        requestQueue.removeIf { it.isFinished }
    }
}