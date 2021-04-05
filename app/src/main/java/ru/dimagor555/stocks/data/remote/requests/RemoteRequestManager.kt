package ru.dimagor555.stocks.data.remote.requests

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRequestManager @Inject constructor(
    private val requestQueue: RemoteRequestQueue,
    private val requestExecutor: RemoteRequestExecutor,
    private val networkErrorHandler: RemoteRequestNetworkErrorHandler,
) {
    @Volatile
    private var requestsBlock = false

    val networkErrorsObservable: Flowable<Exception>
        get() = networkErrorHandler.uiNetworkErrorsObservable

    init {
        requestExecutor.onRequestFinishedCallback =
            Action { onRequestFinished() }

        networkErrorHandler.executorErrors =
            requestExecutor
                .networkErrorsObservable
                .toFlowable(BackpressureStrategy.LATEST)

        networkErrorHandler.onNetworkErrorCallback =
            Action {
                if (!requestsBlock)
                    suspendRequestForMilliseconds(SUSPEND_TIME_ON_CRITICAL_NETWORK_ERROR)
            }

        networkErrorHandler.init()
    }

    fun addRequest(request: RemoteRequest) {
        Log.d(TAG, "addRequest: request = $request")
        if (request is RemoteRequest.Search) {
            throw IllegalArgumentException(
                "Search request must " +
                        "be executed in separate method"
            )
        }
        requestQueue.addRequest(request)
        Log.d(TAG, "request added, requests in progress = ${requestQueue.countOfRequestInProcess}")
        Log.d(TAG, "requestQueue.requestToExecute = ${requestQueue.getRequestToExecute()}")
        notifyMayBePendingRequests()
    }

    @Synchronized
    private fun notifyMayBePendingRequests() {
        Log.d(TAG, "notifyMayBePendingRequests() called")
        if (canMakeRequests()) {
            val request = requestQueue.getRequestToExecute()
            request?.let {
                Log.d(TAG, "requestExecuted: request = $request " +
                        "apiLimit = ${requestExecutor.apiLimitRemaining}")
                requestExecutor.executeRequest(it)
            }
        }
    }

    @Synchronized
    private fun canMakeRequests(): Boolean {
        Log.d(TAG, "canMakeRequests():" +
                "!requestsBlock = ${!requestsBlock} " +
                "requestQueue.countOfRequestInProcess <= MAX_CONCURRENT_REQUESTS = ${requestQueue.countOfRequestInProcess <= MAX_CONCURRENT_REQUESTS}")
        return !requestsBlock &&
                requestQueue.countOfRequestInProcess <= MAX_CONCURRENT_REQUESTS
    }

    fun makeSearchRequest(request: RemoteRequest.Search): Single<List<String>> {
        return requestExecutor.executeSearchRequest(request = request)
    }

    @Synchronized
    private fun onRequestFinished() {
        Log.d(TAG, "onRequestFinished")
        if (requestExecutor.apiLimitRemaining == 0) {
            Log.d(TAG, "apiLimitRemaining = ${requestExecutor.apiLimitRemaining}, " +
                    "apiLimitResetTime = ${requestExecutor.apiLimitResetTime}, " +
                    "currTime = ${System.currentTimeMillis() / 1000}")
            val resetTime = requestExecutor.apiLimitResetTime
            val remainingTime = computeMillisecondsToApiReset(resetTime)
            suspendRequestForMilliseconds(remainingTime)
        } else {
            notifyMayBePendingRequests()
        }
    }

    private fun computeMillisecondsToApiReset(apiLimitResetTime: Int): Long {
        val seconds = apiLimitResetTime - System.currentTimeMillis() / 1000
        val resultSeconds = if (seconds > 1) seconds + 1 else 1
        return resultSeconds * 1000
    }

    private var suspendDisposable: Disposable? = null

    private fun suspendRequestForMilliseconds(time: Long) {
        Log.e(TAG, "suspendRequests for $time ms")
        requestsBlock = true
        suspendDisposable?.dispose()
        suspendDisposable = Completable.complete()
            .subscribeOn(Schedulers.computation())
            .delay(time, TimeUnit.MILLISECONDS)
            .subscribe { continueRequests() }
    }

    private fun continueRequests() {
        Log.e(TAG, "requests continued")
        requestsBlock = false
        requestExecutor.resetApiLimitRemaining()
        notifyMayBePendingRequests()
    }

    companion object {
        private const val MAX_CONCURRENT_REQUESTS = 5
        private const val SUSPEND_TIME_ON_CRITICAL_NETWORK_ERROR: Long = 20_000
        private const val TAG = "RemoteRequestManager"
    }
}