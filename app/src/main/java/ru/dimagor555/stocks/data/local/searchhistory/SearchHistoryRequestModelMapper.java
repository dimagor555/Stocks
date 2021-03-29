package ru.dimagor555.stocks.data.local.searchhistory;

import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;

public class SearchHistoryRequestModelMapper {
    public SearchHistoryRequestModel toModel(SearchHistoryRequest request) {
        return new SearchHistoryRequestModel(request.getRequestText(), request.getTime());
    }

    public SearchHistoryRequest fromModel(SearchHistoryRequestModel model) {
        return new SearchHistoryRequest(model.getRequestText(), model.getTime());
    }
}
