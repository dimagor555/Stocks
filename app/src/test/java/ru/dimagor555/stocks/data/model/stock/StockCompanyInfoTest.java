package ru.dimagor555.stocks.data.model.stock;

import org.junit.Test;
import ru.dimagor555.stocks.data.model.stock.entity.StockCompanyInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StockCompanyInfoTest {
    @Test
    public void isEmpty_Empty_ReturnTrue() {
        StockCompanyInfo stockCompanyInfo = new StockCompanyInfo();

        assertTrue(stockCompanyInfo.isEmpty());
    }

    @Test
    public void isEmpty_NotEmpty_ReturnFalse() {
        StockCompanyInfo stockCompanyInfo = new StockCompanyInfo();

        stockCompanyInfo.setCompanyInfo("1", null, null);
        assertFalse(stockCompanyInfo.isEmpty());

        stockCompanyInfo.setCompanyInfo(null, "1", null);
        assertFalse(stockCompanyInfo.isEmpty());

        stockCompanyInfo.setCompanyInfo(null, null, "1");
        assertFalse(stockCompanyInfo.isEmpty());

        stockCompanyInfo.setCompanyInfo("name", "url", "logo");
        assertFalse(stockCompanyInfo.isEmpty());
    }
}
