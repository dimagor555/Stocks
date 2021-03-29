package ru.dimagor555.stocks.data.remote.responses;

import com.google.gson.annotations.SerializedName;

public class StockPriceResponse {
    @SerializedName("c")
    private final float currPrice;

    @SerializedName("pc")
    private final float previousClosePrice;

    public StockPriceResponse(float currPrice, float previousClosePrice) {
        this.currPrice = currPrice;
        this.previousClosePrice = previousClosePrice;
    }

    public float getCurrPrice() {
        return currPrice;
    }

    public float getPreviousClosePrice() {
        return previousClosePrice;
    }
}
