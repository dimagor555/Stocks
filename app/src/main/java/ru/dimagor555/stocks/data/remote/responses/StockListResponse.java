package ru.dimagor555.stocks.data.remote.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockListResponse {
    @SerializedName("constituents")
    private final List<String> tickers;

    public StockListResponse(List<String> tickers) {
        this.tickers = tickers;
    }

    public List<String> getTickers() {
        return tickers;
    }
}
