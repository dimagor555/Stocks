package ru.dimagor555.stocks.data.model.stock;

public class StockCompanyInfo {
    private String companyName;
    private String companySiteUrl;
    private String logoUrl;

    public StockCompanyInfo() {
    }

    public StockCompanyInfo(String companyName, String companySiteUrl, String logoUrl) {
        setCompanyInfo(companyName, companySiteUrl, logoUrl);
    }

    public void setCompanyInfo(String companyName, String companySiteUrl, String logoUrl) {
        this.companyName = companyName;
        this.companySiteUrl = companySiteUrl;
        this.logoUrl = logoUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanySiteUrl() {
        return companySiteUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean isEmpty() {
        return companyName == null && companySiteUrl == null && logoUrl == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockCompanyInfo that = (StockCompanyInfo) o;

        if (companyName != null ? !companyName.equals(that.companyName) : that.companyName != null) return false;
        if (companySiteUrl != null ? !companySiteUrl.equals(that.companySiteUrl) : that.companySiteUrl != null)
            return false;
        return logoUrl != null ? logoUrl.equals(that.logoUrl) : that.logoUrl == null;
    }

    @Override
    public int hashCode() {
        int result = companyName != null ? companyName.hashCode() : 0;
        result = 31 * result + (companySiteUrl != null ? companySiteUrl.hashCode() : 0);
        result = 31 * result + (logoUrl != null ? logoUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StockCompanyInfo{" +
                "companyName='" + companyName + '\'' +
                ", companySiteUrl='" + companySiteUrl + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                '}';
    }
}
