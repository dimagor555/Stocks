package ru.dimagor555.stocks.data.model.stock;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

public class StockPriceTest {
    @Test
    public void getCurrPrice_SetPrice_ReturnCorrectPrice() {
        StockPrice stockPrice = new StockPrice(12556, 1636, 0);

        String result = stockPrice.getCurrPrice();

        assertEquals("125.56", result);
    }

    @Test
    public void getCurrPrice_SetEmpty_ReturnNull() {
        StockPrice stockPrice = new StockPrice();

        String result = stockPrice.getCurrPrice();

        assertNull(result);
    }

    @Test
    public void getDeltaPrice_SetPositiveDeltaPrice_ReturnCorrectPrice() {
        StockPrice stockPrice = new StockPrice(10521, 10000, 0);

        String result = stockPrice.getDeltaPrice();

        assertEquals("5.21", result);
    }

    @Test
    public void getDeltaPrice_SetNegativeDeltaPrice_ReturnCorrectPrice() {
        StockPrice stockPrice = new StockPrice(10000, 10521, 0);

        String result = stockPrice.getDeltaPrice();

        assertEquals("5.21", result);
    }

    @Test
    public void getDeltaPrice_SetEmpty_ReturnNull() {
        StockPrice stockPrice = new StockPrice();

        String result = stockPrice.getDeltaPrice();

        assertNull(result);
    }

    @Test
    public void getDeltaPricePercent_SetPositiveDeltaPrice_ReturnCorrectPrice() {
        StockPrice stockPrice = new StockPrice(101, 100, 0);

        String result = stockPrice.getDeltaPricePercent();

        assertEquals("1", result);
    }

    @Test
    public void getDeltaPricePercent_SetNegativeDeltaPrice_ReturnCorrectPrice() {
        StockPrice stockPrice = new StockPrice(99, 100, 0);

        String result = stockPrice.getDeltaPricePercent();

        assertEquals("1", result);
    }

    @Test
    public void getDeltaPricePercent_SetEmpty_ReturnNull() {
        StockPrice stockPrice = new StockPrice();

        String result = stockPrice.getDeltaPricePercent();

        assertNull(result);
    }

    @Test
    public void isDeltaPricePositive_CurrPriceMoreThanClosePrice_ReturnTrue() {
        StockPrice stockPrice = new StockPrice(125, 10, 0);

        assertTrue(stockPrice.isDeltaPricePositive());
    }

    @Test
    public void isDeltaPricePositive_CurrPriceLessThanClosePrice_ReturnFalse() {
        StockPrice stockPrice = new StockPrice(10, 125, 0);

        assertFalse(stockPrice.isDeltaPricePositive());
    }

    @Test
    public void isFresh_NowTimeAndIsWeekend_ReturnTrue() {
        StockPrice stockPriceMock = getMockPriceWithIsNowWeekend(
                new StockPrice(1, 1, System.currentTimeMillis()),
                true);

        assertTrue(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_NowTimeAndIsNotWeekend_ReturnTrue() {
        StockPrice stockPriceMock = getMockPriceWithIsNowWeekend(
                new StockPrice(1, 1, System.currentTimeMillis()),
                false);

        assertTrue(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_15MinAgoTimeAndIsNotWeekend_ReturnTrue() {
        StockPrice stockPriceMock = getMockPriceWithIsNowWeekend(
                new StockPrice(1, 1, System.currentTimeMillis() - 15 * 60 * 1000),
                false);

        assertTrue(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_40MinAgoTimeAndIsNotWeekend_ReturnFalse() {
        StockPrice stockPriceMock = getMockPriceWithIsNowWeekend(
                new StockPrice(1, 1, System.currentTimeMillis() - 40 * 60 * 1000),
                false);

        assertFalse(stockPriceMock.isFresh());
    }

    public StockPrice getMockPriceWithIsNowWeekend(StockPrice stockPrice, boolean isWeekend) {
        StockPrice stockPriceMock = spy(stockPrice);
        try {
            when(stockPriceMock, "isNowWeekend").thenReturn(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockPriceMock;
    }
}
