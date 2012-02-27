package se.vgregion.ifeed.service.solr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

public class SolrQueryBuilder {

    public static String createQuery(IFeedFilter iFeedFilter) {
        String query = "";

        // iFeedFilter.getFilterKey()

        Filter filter = iFeedFilter.getFilter();

        System.out.println("filter: " + ToStringBuilder.reflectionToString(filter));

        String filterQuery = iFeedFilter.getFilterQuery();

        if (filter == null || filter.getMetadataType() == null) {

            if (iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.validfrom")) {
                query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
            } else if (iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.validto")) {
                query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
            } else {
                query = iFeedFilter.getFilterKey() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
            }

        } else {
            switch (filter.getMetadataType()) {
                case TEXT_FREE:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
                case TEXT_FIX:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
                case LDAP_VALUE:
                    query = filter.getFilterField() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
                    break;
                case LDAP_ORG_VALUE:
                    query = filter.getFilterField() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
                    break;
                case DATE:
                    if (filter.name().contains("FROM_DATE")) {
                        query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
                    } else if (filter.name().contains("TO_DATE")) {
                        query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
                    } else {
                        throw new RuntimeException("Unable to build query. " + "Unknown filter date type found: "
                                + filter.name());
                    }
                    break;
                default:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
            }
        }
        return query;
    }

    public static String createQuery(IFeedFilter iFeedFilter, List<FieldInf> infs) {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    static {

    }

    public static interface Processor {
        String format(String field, String value);

        // Processor pro = new Processor() {
        // @Override
        // public String format(String field, String value) {
        // if (filter.name().contains("d:date")) {
        // query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
        // } else if (filter.name().contains("TO_DATE")) {
        // query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
        // } else {
        // throw new RuntimeException("Unable to build query. " + "Unknown filter date type found: "
        // + filter.name());
        // }
        // }
        // };
    }

    private static Map<String, Processor> processors = new HashMap<String, SolrQueryBuilder.Processor>();

}
