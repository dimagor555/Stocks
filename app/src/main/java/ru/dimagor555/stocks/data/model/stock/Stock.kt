package ru.dimagor555.stocks.data.model.stock

data class Stock(
    val ticker: String,
    val companyInfo: StockCompanyInfo = StockCompanyInfo(),
    val price: StockPrice = StockPrice(),
    private var _isFavourite: Boolean = false,
) {
    val isFavourite
        get() = _isFavourite

    fun toggleFavourite() {
        _isFavourite = !_isFavourite
    }
}