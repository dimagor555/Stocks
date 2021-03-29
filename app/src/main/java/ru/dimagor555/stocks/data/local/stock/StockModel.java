package ru.dimagor555.stocks.data.local.stock;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stocks")
public class StockModel {
    @NonNull
    @PrimaryKey
    private String ticker;

    private String companyName;
    private String companySiteUrl;
    private String logoUrl;

    //Storing price in cents for solving float point issue with money
    private int currPriceInCents;
    private int previousClosePriceInCents;
    private long priceTime;

    private boolean favourite;

    public StockModel(String ticker, String companyName, String companySiteUrl, String logoUrl,
                      int currPriceInCents, int previousClosePriceInCents,
                      long priceTime, boolean favourite) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.companySiteUrl = companySiteUrl;
        this.logoUrl = logoUrl;
        this.currPriceInCents = currPriceInCents;
        this.previousClosePriceInCents = previousClosePriceInCents;
        this.priceTime = priceTime;
        this.favourite = favourite;
    }

    public String getTicker() {
        return ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanySiteUrl() {
        return companySiteUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public int getCurrPriceInCents() {
        return currPriceInCents;
    }

    public int getPreviousClosePriceInCents() {
        return previousClosePriceInCents;
    }

    public long getPriceTime() {
        return priceTime;
    }

    public boolean isFavourite() {
        return favourite;
    }
}
