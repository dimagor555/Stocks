package ru.dimagor555.stocks.data.model.searchhistory;

import org.junit.Assert;
import org.junit.Test;

public class SearchHistoryRequestTest {
    SearchHistoryRequest request = new SearchHistoryRequest("Test", 10000);

    @Test
    public void olderThan_FirstOlder_ReturnTrue() {
        boolean result = request.olderThan(new SearchHistoryRequest("Test", 20000));

        Assert.assertTrue(result);
    }

    @Test
    public void olderThan_SecondOlder_ReturnFalse() {
        boolean result = request.olderThan(new SearchHistoryRequest("Test", 1000));

        Assert.assertFalse(result);
    }

    @Test
    public void olderThan_BothEquals_ReturnFalse() {
        boolean result = request.olderThan(new SearchHistoryRequest("Test", request.getTime()));

        Assert.assertFalse(result);
    }
}
