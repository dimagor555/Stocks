package ru.dimagor555.stocks.data.model.stock;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class StockTest {
    @Test
    public void toggleFavouriteTest() {
        Stock stock = new Stock("test");

        assertFalse(stock.isFavourite());

        stock.toggleFavourite();

        assertTrue(stock.isFavourite());

        stock.toggleFavourite();

        assertFalse(stock.isFavourite());
    }
}
