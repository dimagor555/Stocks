package ru.dimagor555.stocks.data.remote

import android.util.Log
import io.reactivex.Single
import retrofit2.HttpException
import retrofit2.Response
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException
import ru.dimagor555.stocks.data.remote.exception.UnknownErrorException
import ru.dimagor555.stocks.data.remote.responses.BaseResponse
import java.io.IOException

object RemoteUtils {
    fun <T> handleResponseErrors(
        response: Response<*>,
        data: T?
    ): Single<BaseResponse<T>> {
        return if (response.isSuccessful) {
            Single.just(BaseResponse(response, data!!))
        } else {
            Single.error(HttpException(response))
        }
    }

    fun <T> wrapApiCallException(throwable: Throwable): Single<T> {
        return if (throwable is IOException) {
            Single.error(NetworkErrorException())
        } else if (throwable is HttpException) {
            if (throwable.code() == 429) {
                Single.error(ApiLimitReachedException())
            } else {
                Single.error(UnknownErrorException(throwable.message))
            }
        } else {
            Log.e("NETWORK", throwable.message, throwable)
            throw RuntimeException(throwable)
        }
    }
}