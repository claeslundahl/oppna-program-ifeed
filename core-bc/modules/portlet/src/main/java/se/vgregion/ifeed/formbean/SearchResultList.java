package se.vgregion.ifeed.formbean;

import java.util.AbstractList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.DateFormatter.DateFormats;
import se.vgregion.ifeed.service.solr.DateUtils;

public class SearchResultList extends AbstractList<SearchResultList.SearchResult> {
    private List<Map<String, Object>> iFeedResults = null;
    private Iterator<Map<String, Object>> iterator = null;

    public SearchResultList(List<Map<String, Object>> iFeedResults) {
        this.iFeedResults = iFeedResults;
        iterator = iFeedResults.iterator();
    }

    public static class SearchResult {
        private Date updated;
        private String title;

        public SearchResult(Date updated, String title) {
            this.updated = updated;
            this.title = title;
        }

        public String getUpdated() {
            if(DateUtils.isToday(updated)) {
                return DateFormatter.format(updated, DateFormats.TIME_ONLY);
            } else {
                return DateFormatter.format(updated, DateFormats.DATE_ONLY);
            }
        }

        public String getTitle() {
            return title;
        }
    }

    @Override
    public SearchResult get(int index) {
        final Map<String, Object> resultEntry = iFeedResults.get(index);
        final Date date = (Date)resultEntry.get("processingtime");
        final String title = (String)resultEntry.get("title");
        return new SearchResult(new Date(date.getTime()), title);
    }

    @Override
    public int size() {
        return iFeedResults.size();
    }
}
