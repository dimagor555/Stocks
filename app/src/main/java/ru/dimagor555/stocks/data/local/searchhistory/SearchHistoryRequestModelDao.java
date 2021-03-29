package ru.dimagor555.stocks.data.local.searchhistory;

import androidx.room.*;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;

import java.util.List;

@Dao
public interface SearchHistoryRequestModelDao {
    @Query("select * from searchHistoryRequests order by time desc")
    Flowable<List<SearchHistoryRequestModel>> getAllRequestsFlowable();

    @Query("select * from searchHistoryRequests order by time desc")
    List<SearchHistoryRequest> getAllRequests();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(SearchHistoryRequestModel requestModel);

    @Delete
    Completable delete(SearchHistoryRequestModel requestModel);
}
