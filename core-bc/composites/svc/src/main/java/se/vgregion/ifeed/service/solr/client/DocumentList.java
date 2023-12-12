package se.vgregion.ifeed.service.solr.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentList {
        private int numberOfHits;

    private Pagination pagination = new Pagination();

    private String id, displayName;

        //private int start;
        private List<Map<String, Object>> documents = new ArrayList<>();

        public int getNumberOfHits() {
            return numberOfHits;
        }

        public void setNumberOfHits(int numberOfHits) {
            this.numberOfHits = numberOfHits;
        }

        public List<Map<String, Object>> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Map<String, Object>> docs) {
            this.documents = docs;
        }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}