package ru.dimagor555.stocks.data.model.searchhistory;

public class SearchHistoryRequest {
    private final String requestText;
    private final long time;

    public SearchHistoryRequest(String requestText, long time) {
        this.requestText = requestText;
        this.time = time;
    }

    public String getRequestText() {
        return requestText;
    }

    public long getTime() {
        return time;
    }

    public boolean olderThan(SearchHistoryRequest that) {
        return this.time < that.time;
    }
}
