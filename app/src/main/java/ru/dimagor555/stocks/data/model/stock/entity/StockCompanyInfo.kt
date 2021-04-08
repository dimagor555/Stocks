package ru.dimagor555.stocks.data.model.stock.entity

data class StockCompanyInfo(
    private var _companyName: String? = null,
    private var _companySiteUrl: String? = null,
    private var _logoUrl: String? = null,
) {
    val companyName: String?
        get() = _companyName
    val companySiteUrl: String?
        get() = _companySiteUrl
    val logoUrl: String?
        get() = _logoUrl

    fun setCompanyInfo(companyName: String?, companySiteUrl: String?, logoUrl: String?) {
        _companyName = companyName
        _companySiteUrl = companySiteUrl
        _logoUrl = logoUrl
    }

    val isEmpty: Boolean
        get() = _companyName == null && _companySiteUrl == null && _logoUrl == null
}