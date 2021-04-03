package ru.dimagor555.stocks.data.model.stock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Clock;
import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(StockPrice.class)
@RunWith(PowerMockRunner.class)
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
        mockWeekend(true);
        StockPrice stockPriceMock = new StockPrice(1, 1,
                System.currentTimeMillis());

        assertTrue(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_NowTimeAndIsNotWeekend_ReturnTrue() {
        mockWeekend(false);
        StockPrice stockPriceMock = new StockPrice(1, 1,
                System.currentTimeMillis());

        assertTrue(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_15MinAgoTimeAndIsNotWeekend_ReturnTrue() {
        mockWeekend(false);
        StockPrice stockPriceMock = new StockPrice(1, 1,
                System.currentTimeMillis() - 15 * 60 * 1000);

        assertTrue(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_40MinAgoTimeAndIsNotWeekend_ReturnFalse() {
        mockWeekend(false);
        StockPrice stockPriceMock = new StockPrice(1, 1,
                System.currentTimeMillis() - 40 * 60 * 1000);

        assertFalse(stockPriceMock.isFresh());
    }

    @Test
    public void isFresh_40MinAgoTimeAndIsWeekend_ReturnTrue() {
        mockWeekend(true);
        StockPrice stockPriceMock = new StockPrice(1, 1,
                System.currentTimeMillis() - 40 * 60 * 1000);

        assertTrue(stockPriceMock.isFresh());
    }

    public void mockWeekend(boolean isWeekend) {
        LocalDate dateToMock;
        if (isWeekend) {
            dateToMock = LocalDate.of(2021, 4, 4);
        } else {
            dateToMock = LocalDate.of(2021, 4, 1);
        }

        mockStatic(LocalDate.class);
        when(LocalDate.now(Clock.systemUTC())).thenReturn(dateToMock);
    }
}
