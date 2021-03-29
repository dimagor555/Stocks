package ru.dimagor555.stocks.data.model.stock;

public class Stock {
    private final String ticker;
    private final StockCompanyInfo companyInfo;
    private final StockPrice price;
    private boolean favourite;

    public Stock(String ticker) {
        this.ticker = ticker;
        companyInfo = new StockCompanyInfo();
        price = new StockPrice();
    }

    public Stock(String ticker, String companyName, String companySiteUrl, String logoUrl,
                 int currPriceInCents, int previousClosePriceInCents,
                 long priceTime, boolean favourite) {
        this(ticker);
        companyInfo.setCompanyInfo(companyName, companySiteUrl, logoUrl);
        price.setPrice(currPriceInCents, previousClosePriceInCents, priceTime);
        this.favourite = favourite;
    }

    public String getTicker() {
        return ticker;
    }

    public StockCompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public StockPrice getPrice() {
        return price;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void toggleFavourite() {
        favourite = !favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stock stock = (Stock) o;

        if (favourite != stock.favourite) return false;
        if (!ticker.equals(stock.ticker)) return false;
        if (!companyInfo.equals(stock.companyInfo)) return false;
        return price.equals(stock.price);
    }

    @Override
    public int hashCode() {
        int result = ticker.hashCode();
        result = 31 * result + companyInfo.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + (favourite ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", companyInfo=" + companyInfo +
                ", price=" + price +
                ", favourite=" + favourite +
                '}';
    }
}
