package se.vgregion.ifeed.service.solr.client;

public class Result {

    private Stats stats = new Stats();

        private Header responseHeader;
        private DocumentList documentList = new DocumentList();

        public Header getResponseHeader() {
            return responseHeader;
        }

        public void setResponseHeader(Header responseHeader) {
            this.responseHeader = responseHeader;
        }

        public DocumentList getDocumentList() {
            return documentList;
        }

        public void setDocumentList(DocumentList documentList) {
            this.documentList = documentList;
        }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
}