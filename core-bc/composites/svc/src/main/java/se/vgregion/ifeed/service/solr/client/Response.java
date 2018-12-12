package se.vgregion.ifeed.service.solr.client;

import java.util.List;
import java.util.Map;

public class Response {
        private int numFound;

        private int start;
        private List<Map<String, Object>> docs;

        public int getNumFound() {
            return numFound;
        }

        public void setNumFound(int numFound) {
            this.numFound = numFound;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public List<Map<String, Object>> getDocs() {
            return docs;
        }

        public void setDocs(List<Map<String, Object>> docs) {
            this.docs = docs;
        }

    }