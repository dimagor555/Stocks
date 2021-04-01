package ru.dimagor555.stocks.data.local.searchhistory;

import android.util.Log;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class LocalSearchHistoryRequestDatasource {
    private final SearchHistoryRequestModelDao dao;
    private final SearchHistoryRequestModelMapper mapper;

    @Inject
    public LocalSearchHistoryRequestDatasource(SearchHistoryRequestModelDao dao,
                                               SearchHistoryRequestModelMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    public Flowable<List<SearchHistoryRequest>> getAllRequestsFlowable() {
        return dao.getAllRequestsFlowable()
                .subscribeOn(Schedulers.io())
                .flatMap(models -> {
                    List<SearchHistoryRequest> requests = new LinkedList<>();
                    models.forEach(model -> requests.add(mapper.fromModel(model)));
                    return Flowable.just(requests);
                });
    }

    public List<SearchHistoryRequest> getAllRequests() {
        return dao.getAllRequests();
    }

    public void insertOrUpdate(SearchHistoryRequest request) {
        dao.insert(mapper.toModel(request))
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Log.e("DB", throwable.getMessage(), throwable))
                .subscribe();
    }

    public void delete(SearchHistoryRequest request) {
        dao.delete(mapper.toModel(request))
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Log.e("DB", throwable.getMessage(), throwable))
                .subscribe();
    }
}
