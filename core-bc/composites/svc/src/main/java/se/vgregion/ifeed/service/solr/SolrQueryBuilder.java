package se.vgregion.ifeed.service.solr;

import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

public class SolrQueryBuilder {

    public static String createQuery(IFeedFilter iFeedFilter) {
        String query = "";
        Filter filter = iFeedFilter.getFilter();
        String filterQuery = iFeedFilter.getFilterQuery();
        switch (filter.getMetadataType()) {
            case TEXT_FREE:
                query  = filter.getFilterField() + ":\""
                    + SolrQueryEscaper.escape(filterQuery) + "\"";
                break;
            case TEXT_FIX:
                query  = filter.getFilterField() + ":"
                    + SolrQueryEscaper.escape(filterQuery) + "";
                break;
            case DATE:
                if (filter.name().contains("FROM_DATE")) {
                    query = filter.getFilterField()
                        + ":[" + filterQuery + " TO *]";
                } else if (filter.name().contains("TO_DATE")) {
                    query = filter.getFilterField()
                        + ":[* TO " + filterQuery + "]";
                } else {
                    throw new RuntimeException(
                        "Unable to build query. "
                            + "Unknown filter date type found: "
                                + filter.name());
                }
                break;
            default:
                break;
        }
        return query;
    }
}
