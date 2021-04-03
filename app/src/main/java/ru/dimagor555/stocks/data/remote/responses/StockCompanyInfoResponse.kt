package ru.dimagor555.stocks.data.remote.responses

import com.google.gson.annotations.SerializedName

class StockCompanyInfoResponse(
    @SerializedName("ticker") val ticker: String,
    @SerializedName("name") val companyName: String,
    @SerializedName("logo") val logoUrl: String,
    @SerializedName("weburl") val companySiteUrl: String,
)