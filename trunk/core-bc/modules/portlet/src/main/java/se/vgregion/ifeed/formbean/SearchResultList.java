package se.vgregion.ifeed.formbean;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.DateFormatter.DateFormats;
import se.vgregion.ifeed.service.solr.DateUtils;

public class SearchResultList extends AbstractList<SearchResultList.SearchResult> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Map<String, Object>> iFeedResults = null;
    private Iterator<Map<String, Object>> iterator = null;

    public SearchResultList(List<Map<String, Object>> iFeedResults) {
        this.iFeedResults = iFeedResults;
        iterator = iFeedResults.iterator();
    }

    public static class SearchResult {
        private Date processingTime;
        private String title;
        private String link;
        private String documentId;

        public SearchResult(Date processingTime, String title, String link, String documentId) {
            this.processingTime = processingTime;
            this.title = title;
            this.link = link;
            this.documentId = documentId;
        }

        public String getProcessingTime() {
            if(DateUtils.isToday(processingTime)) {
                return DateFormatter.format(processingTime, DateFormats.TIME_ONLY);
            } else {
                return DateFormatter.format(processingTime, DateFormats.DATE_ONLY);
            }
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDocumentId() {
            return documentId;
        }
    }

    @Override
    public SearchResult get(int index) {
        final Map<String, Object> resultEntry = iFeedResults.get(index);
        final String documentId = (String)resultEntry.get("dc.identifier.documentid");
        final Date date = (Date)resultEntry.get("processingtime");
        final String title = (String)resultEntry.get("title");
        final String link = (String)resultEntry.get("url");
        return new SearchResult(new Date(date.getTime()), title, link, documentId);
    }

    @Override
    public int size() {
        return iFeedResults.size();
    }
}
