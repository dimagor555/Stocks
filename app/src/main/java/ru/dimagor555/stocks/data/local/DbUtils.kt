package ru.dimagor555.stocks.data.local

import android.util.Log
import io.reactivex.Completable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

object DbUtils {
    fun runDbQuery(action: Action) {
        Completable.fromAction(action)
            .subscribeOn(Schedulers.io())
            .doOnError { logDbError(it) }
            .subscribe()
    }

    private fun logDbError(error: Throwable) = Log.e("DB", error.message, error)
}