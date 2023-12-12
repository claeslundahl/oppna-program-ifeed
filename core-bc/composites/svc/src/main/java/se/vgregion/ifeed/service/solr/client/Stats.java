package se.vgregion.ifeed.service.solr.client;

public class Stats {
    private long totalHits, searchEngineTimeInMillis, searchEngineRoundTripTimeInMillis, searchProcessingTimeInMillis;

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    public long getSearchEngineTimeInMillis() {
        return searchEngineTimeInMillis;
    }

    public void setSearchEngineTimeInMillis(long searchEngineTimeInMillis) {
        this.searchEngineTimeInMillis = searchEngineTimeInMillis;
    }

    public long getSearchEngineRoundTripTimeInMillis() {
        return searchEngineRoundTripTimeInMillis;
    }

    public void setSearchEngineRoundTripTimeInMillis(long searchEngineRoundTripTimeInMillis) {
        this.searchEngineRoundTripTimeInMillis = searchEngineRoundTripTimeInMillis;
    }

    public long getSearchProcessingTimeInMillis() {
        return searchProcessingTimeInMillis;
    }

    public void setSearchProcessingTimeInMillis(long searchProcessingTimeInMillis) {
        this.searchProcessingTimeInMillis = searchProcessingTimeInMillis;
    }
}
