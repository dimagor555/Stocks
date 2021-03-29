package ru.dimagor555.stocks.data.model.searchhistory;

import io.reactivex.Flowable;

import java.util.List;

public interface SearchHistoryRepository {
    Flowable<List<SearchHistoryRequest>> getAllRequests();

    void addRequest(SearchHistoryRequest request);

    void deleteRequest(SearchHistoryRequest request);
}
