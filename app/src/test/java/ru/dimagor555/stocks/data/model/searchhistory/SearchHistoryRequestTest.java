package ru.dimagor555.stocks.data.model.searchhistory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchHistoryRequestTest {
    SearchHistoryRequest request;

    @Before
    public void setUp() {
        request = new SearchHistoryRequest("Test", 10000);
    }

    @Test
    public void olderThan_FirstOlder_ReturnTrue() {
        boolean result = request.olderThan(new SearchHistoryRequest("Test", 20000));

        assertTrue(result);
    }

    @Test
    public void olderThan_SecondOlder_ReturnFalse() {
        boolean result = request.olderThan(new SearchHistoryRequest("Test", 1000));

        assertFalse(result);
    }

    @Test
    public void olderThan_BothEquals_ReturnFalse() {
        boolean result = request.olderThan(new SearchHistoryRequest("Test", request.getTime()));

        assertFalse(result);
    }
}
