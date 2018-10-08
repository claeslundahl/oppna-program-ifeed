package se.vgregion.ifeed.service.ifeed;

import se.vgregion.ifeed.service.solr.SolrHttpClient;

public class DocumentPopupConf extends SolrHttpClient.Field {

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }



}
