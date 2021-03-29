package ru.dimagor555.stocks.data.remote;

public class RemoteRequest implements Comparable<RemoteRequest> {
    private boolean inProgress = false;

    // hide the private constructor to limit subclass types
    private RemoteRequest() {
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    @Override
    public int compareTo(RemoteRequest o) {
        if (this.getClass() == o.getClass()) return 0;

        if (this instanceof AllStocks) return -1;

        if (this instanceof CompanyInfo) {
            if (o instanceof AllStocks) return 1;
            else if (o instanceof Price) return -1;
        }

        if (this instanceof Price) return 1;

        return 0;
    }

    private static class TickerRemoteRequest extends RemoteRequest {
        private final String ticker;

        public TickerRemoteRequest(String ticker) {
            this.ticker = ticker;
        }

        public String getTicker() {
            return ticker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TickerRemoteRequest that = (TickerRemoteRequest) o;

            return ticker.equals(that.ticker);
        }

        @Override
        public int hashCode() {
            return ticker.hashCode();
        }
    }

    public static class AllStocks extends RemoteRequest {
    }

    public static class CompanyInfo extends TickerRemoteRequest {
        public CompanyInfo(String ticker) {
            super(ticker);
        }
    }

    public static class Price extends TickerRemoteRequest {
        public Price(String ticker) {
            super(ticker);
        }
    }
}
