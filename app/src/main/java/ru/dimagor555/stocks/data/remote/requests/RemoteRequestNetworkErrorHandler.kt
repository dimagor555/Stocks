package ru.dimagor555.stocks.data.remote.requests

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException
import javax.inject.Inject

class RemoteRequestNetworkErrorHandler @Inject constructor() {
    lateinit var executorErrors: Flowable<Throwable>

    private val uiNetworkErrorsSubject = PublishSubject.create<Exception>()

    val uiNetworkErrorsObservable: Flowable<Exception>
        get() = uiNetworkErrorsSubject
            .toFlowable(BackpressureStrategy.LATEST)

    lateinit var onNetworkErrorCallback: Action

    private val disposeBag = CompositeDisposable()

    fun init() {
        disposeBag.add(
            executorErrors
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleError)
        )
    }

    private var prevException: Throwable? = null

    private fun handleError(error: Throwable) {
        if (
            prevException !is NetworkErrorException
            || error !is NetworkErrorException
        ) {
            uiNetworkErrorsSubject.onNext(error as Exception)
        }
        onNetworkErrorCallback.run()
    }

    fun dispose() {
        disposeBag.dispose()
    }
}