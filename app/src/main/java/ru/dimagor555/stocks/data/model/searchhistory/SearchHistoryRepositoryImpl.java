package ru.dimagor555.stocks.data.model.searchhistory;

import io.reactivex.Flowable;
import ru.dimagor555.stocks.data.local.searchhistory.LocalSearchHistoryRequestDatasource;

import javax.inject.Inject;
import java.util.List;

public class SearchHistoryRepositoryImpl implements SearchHistoryRepository {
    private static final int REQUESTS_HISTORY_LIMIT = 40;

    private final LocalSearchHistoryRequestDatasource localDatasource;

    @Inject
    public SearchHistoryRepositoryImpl(LocalSearchHistoryRequestDatasource localDatasource) {
        this.localDatasource = localDatasource;
    }

    @Override
    public Flowable<List<SearchHistoryRequest>> getAllRequests() {
        return localDatasource.getAllRequestsFlowable();
    }

    @Override
    public void addRequest(SearchHistoryRequest request) {
        List<SearchHistoryRequest> requests = localDatasource.getAllRequests();
        localDatasource.insertOrUpdate(request);
        if (requests.size() >= REQUESTS_HISTORY_LIMIT) {
            deleteOldestRequest(requests);
        }

    }

    private void deleteOldestRequest(List<SearchHistoryRequest> requests) {
        SearchHistoryRequest oldestRequest = requests.get(0);
        for (SearchHistoryRequest currRequest : requests) {
            oldestRequest =
                    currRequest.olderThan(oldestRequest)
                            ? currRequest
                            : oldestRequest;
        }
        localDatasource.delete(oldestRequest);
    }

    @Override
    public void deleteRequest(SearchHistoryRequest request) {
        localDatasource.delete(request);
    }
}
