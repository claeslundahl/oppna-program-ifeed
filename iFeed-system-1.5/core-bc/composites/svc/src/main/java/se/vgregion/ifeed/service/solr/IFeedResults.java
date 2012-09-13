package se.vgregion.ifeed.service.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IFeedResults extends ArrayList<Map<String, Object>> implements List<Map<String, Object>> {

    private static final long serialVersionUID = 1L;

    private String queryUrl;

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrlPart) {
        this.queryUrl = queryUrlPart;
    }

}
