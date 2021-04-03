package ru.dimagor555.stocks.data.remote.requests

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource
import ru.dimagor555.stocks.data.model.stock.Stock
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo
import ru.dimagor555.stocks.data.model.stock.StockPrice
import ru.dimagor555.stocks.data.remote.RemoteStockDatasource
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException
import ru.dimagor555.stocks.data.remote.responses.BaseResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRequestExecutor @Inject constructor(
    private val remoteStockDatasource: RemoteStockDatasource,
    private val localStockDatasource: LocalStockDatasource
) {
    var apiLimitRemaining = 60
        private set
    var apiLimitResetTime = 0
        private set

    val networkErrorsObservable = PublishSubject.create<Throwable>()

    lateinit var onRequestFinishedCallback: Action

    private val disposeBag = CompositeDisposable()

    fun executeRequest(request: RemoteRequest) {
        if (!canExecuteRequest()) return

        request.isInProgress = true
        when (request) {
            is RemoteRequest.AllStocks -> executeAllStocksRequest(request)
            is RemoteRequest.Search ->
                throw IllegalArgumentException(
                    "Search request must " +
                            "be executed in separate method"
                )
            is RemoteRequest.CompanyInfo -> executeCompanyInfoRequest(request)
            is RemoteRequest.Price -> executePriceRequest(request)
        }
    }

    private fun executeAllStocksRequest(request: RemoteRequest.AllStocks) {
        disposeBag.add(
            buildDefaultRequest(
                remoteStockDatasource.allStocks, request
            ).subscribe(
                this::handleAllStocksResult,
                this::handleRemoteError
            )
        )
    }

    private fun handleAllStocksResult(response: BaseResponse<List<Stock>>) {
        response.data.forEach { localStockDatasource.insertStock(it) }
    }

    private fun executeCompanyInfoRequest(request: RemoteRequest.CompanyInfo) {
        val ticker = request.ticker
        disposeBag.add(
            buildDefaultRequest(
                remoteStockDatasource.getStockCompanyInfo(ticker),
                request
            ).subscribe(
                { handleCompanyInfoResult(ticker, it) },
                this::handleRemoteError
            )
        )
    }

    private fun handleCompanyInfoResult(
        ticker: String,
        response: BaseResponse<StockCompanyInfo>
    ) {
        localStockDatasource.updateStockCompanyInfo(ticker, response.data)
    }

    private fun executePriceRequest(request: RemoteRequest.Price) {
        val ticker = request.ticker
        disposeBag.add(
            buildDefaultRequest(
                remoteStockDatasource.getStockPrice(ticker),
                request
            ).subscribe(
                { handlePriceResult(ticker, it) },
                this::handleRemoteError
            )
        )
    }

    private fun handlePriceResult(
        ticker: String,
        response: BaseResponse<StockPrice>
    ) {
        localStockDatasource.updateStockPriceInfo(ticker, response.data)
    }

    //returns list of found tickers
    fun executeSearchRequest(request: RemoteRequest.Search): Single<List<String>> {
        if (!canExecuteRequest()) throw ApiLimitReachedException()

        request.isInProgress = true
        val response = SingleSubject.create<List<String>>()
        val query = request.query
        disposeBag.add(
            buildDefaultRequest(
                remoteStockDatasource.findStockByTickerAndCompanyName(query),
                request
            ).subscribe(
                {
                    handleSearchResult(it)
                    response.onSuccess(it.data)
                },
                {
                    handleRemoteError(it)
                    response.onError(it)
                }
            )
        )
        return response
    }

    private fun handleSearchResult(response: BaseResponse<List<String>>) {
        response.data.forEach { localStockDatasource.insertStock(Stock(it)) }
    }

    private fun <T> buildDefaultRequest(
        request: Single<BaseResponse<T>>,
        requestInfo: RemoteRequest
    ): Single<BaseResponse<T>> {
        return request
            .subscribeOn(Schedulers.io())
            .doAfterSuccess {
                requestInfo.isFinished = true
                updateApiLimit(it.remainingLimit, it.limitResetTime)
            }
            .doOnError { requestInfo.isInProgress = false }
            .doAfterTerminate(onRequestFinishedCallback)
    }

    private fun handleRemoteError(error: Throwable) {
        networkErrorsObservable.onNext(error)
    }

    @Synchronized
    private fun updateApiLimit(remainingLimit: Int, limitResetTime: Int) {
        if (limitResetTime > apiLimitResetTime) {
            apiLimitResetTime = limitResetTime
            apiLimitRemaining = remainingLimit
        } else if (
            apiLimitResetTime == limitResetTime
            && remainingLimit < apiLimitRemaining
        ) {
            apiLimitRemaining = remainingLimit
        }
    }

    @Synchronized
    private fun canExecuteRequest(): Boolean = apiLimitRemaining > 0

    fun resetApiLimitRemaining() {
        apiLimitRemaining = 60
    }

    fun dispose() {
        disposeBag.dispose()
    }
}
