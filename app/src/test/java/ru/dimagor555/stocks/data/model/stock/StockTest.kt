package ru.dimagor555.stocks.data.model.stock

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.dimagor555.stocks.data.model.stock.entity.Stock

class StockTest {
    @Test
    fun `toggle favourite test`() {
        val stock = Stock("test")

        assertFalse(stock.isFavourite)
        stock.toggleFavourite()

        assertTrue(stock.isFavourite)
        stock.toggleFavourite()

        assertFalse(stock.isFavourite)
    }
}