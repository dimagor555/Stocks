package ru.dimagor555.stocks.data.local.searchhistory;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "searchHistoryRequests")
public class SearchHistoryRequestModel {
    @NonNull
    @PrimaryKey
    private final String requestText;

    private final long time;

    public SearchHistoryRequestModel(@NonNull String requestText, long time) {
        this.requestText = requestText;
        this.time = time;
    }

    @NonNull
    public String getRequestText() {
        return requestText;
    }

    public long getTime() {
        return time;
    }
}
