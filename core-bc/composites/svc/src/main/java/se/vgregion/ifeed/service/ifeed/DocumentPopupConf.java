package se.vgregion.ifeed.service.ifeed;


import se.vgregion.ifeed.service.solr.client.Field;

public class DocumentPopupConf extends Field {

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
}
