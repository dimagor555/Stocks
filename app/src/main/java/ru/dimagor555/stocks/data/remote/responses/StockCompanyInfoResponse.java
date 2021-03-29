package ru.dimagor555.stocks.data.remote.responses;

import com.google.gson.annotations.SerializedName;

public class StockCompanyInfoResponse {
    @SerializedName("ticker")
    private final String ticker;

    @SerializedName("name")
    private final String companyName;

    @SerializedName("logo")
    private final String logoUrl;

    @SerializedName("weburl")
    private final String companySiteUrl;

    public StockCompanyInfoResponse(String ticker, String companyName,
                                    String logoUrl, String companySiteUrl) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.logoUrl = logoUrl;
        this.companySiteUrl = companySiteUrl;
    }

    public String getTicker() {
        return ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getCompanySiteUrl() {
        return companySiteUrl;
    }
}
