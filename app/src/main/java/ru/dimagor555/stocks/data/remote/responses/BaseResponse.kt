package ru.dimagor555.stocks.data.remote.responses

import retrofit2.Response

class BaseResponse<T>(
    response: Response<out Any>,
    val data: T,
) {
    val remainingLimit = response.headers()["x-ratelimit-remaining"]!!.toInt()
    val limitResetTime = response.headers()["x-ratelimit-reset"]!!.toInt()
}