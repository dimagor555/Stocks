package ru.dimagor555.stocks.data.model.stock;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class StockPrice {
    private static final long PRICE_EXPIRE_INTERVAL_IN_MINUTES = 30;

    //Storing price in cents for solving float point issue with money
    private int currPriceInCents;
    private int previousClosePriceInCents;
    private int deltaPriceInCents;
    private long priceTime;

    public StockPrice() {
    }

    public StockPrice(int currPriceInCents, int previousClosePriceInCents, long priceTime) {
        setPrice(currPriceInCents, previousClosePriceInCents, priceTime);
    }

    public void setPrice(int currPriceInCents, int previousClosePriceInCents, long priceTime) {
        this.currPriceInCents = currPriceInCents;
        this.previousClosePriceInCents = previousClosePriceInCents;
        this.deltaPriceInCents = currPriceInCents - previousClosePriceInCents;
        this.priceTime = priceTime;
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

    public String getCurrPrice() {
        if (!isEmpty()) {
            return priceInCentsToString(currPriceInCents);
        } else {
            return null;
        }
    }

    public String getDeltaPrice() {
        if (!isEmpty()) {
            return priceInCentsToString(deltaPriceInCents);
        } else {
            return null;
        }
    }

    public String getDeltaPricePercent() {
        if (!isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            return decimalFormat.format(Math.abs(deltaPriceInCents)
                    / (previousClosePriceInCents / 100f));
        } else {
            return null;
        }
    }

    private String priceInCentsToString(int price) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(Math.abs(price) / 100d);
    }

    public boolean isDeltaPricePositive() {
        return deltaPriceInCents >= 0;
    }

    public boolean isFresh() {
        return (isPriceTimeFresh() || isNowWeekend()) && !isEmpty();
    }

    private boolean isPriceTimeFresh() {
        return (System.currentTimeMillis() - priceTime) / 1000 / 60 <
                PRICE_EXPIRE_INTERVAL_IN_MINUTES;
    }

    //no trading & price info at the weekend
    private boolean isNowWeekend() {
        DayOfWeek nowDayOfWeek = LocalDate.now(Clock.systemUTC()).getDayOfWeek();
        return nowDayOfWeek == DayOfWeek.SUNDAY || nowDayOfWeek == DayOfWeek.SATURDAY;
    }

    private boolean isEmpty() {
        return currPriceInCents == 0 && deltaPriceInCents == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockPrice that = (StockPrice) o;

        if (currPriceInCents != that.currPriceInCents) return false;
        if (previousClosePriceInCents != that.previousClosePriceInCents) return false;
        if (deltaPriceInCents != that.deltaPriceInCents) return false;
        return priceTime == that.priceTime;
    }

    @Override
    public int hashCode() {
        int result = currPriceInCents;
        result = 31 * result + previousClosePriceInCents;
        result = 31 * result + deltaPriceInCents;
        result = 31 * result + (int) (priceTime ^ (priceTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "StockPrice{" +
                "currPriceInCents=" + currPriceInCents +
                ", previousClosePriceInCents=" + previousClosePriceInCents +
                ", deltaPriceInCents=" + deltaPriceInCents +
                ", priceTime=" + priceTime +
                '}';
    }
}
