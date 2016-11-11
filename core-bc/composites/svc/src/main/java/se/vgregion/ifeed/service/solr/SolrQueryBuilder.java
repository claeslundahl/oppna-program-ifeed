package se.vgregion.ifeed.service.solr;

import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

import java.text.SimpleDateFormat;
import java.util.*;

public class SolrQueryBuilder {

    public static String createOrQuery(Collection<IFeedFilter> filters) {
        //fq=(dc.contributor.savedby.id%3Asusro3 OR dc.contributor.savedby.id%3Aclalu4)
        if (filters.isEmpty()) {
            throw new RuntimeException("No filter provided");
        }
        if (filters.size() == 1) {
            return createQuery(filters.iterator().next(), (Map<String, FieldInf>) null);
        }
        List<String> parts = new ArrayList<String>();
        for (IFeedFilter filter : filters) {
            parts.add(createQuery(filter, (Map<String, FieldInf>) null));
        }
        return "((" + se.vgregion.ifeed.service.ifeed.Filter.join(parts, ") OR (") + "))";
    }

    /*private static String join(List<?> list, String junctor) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0) + "";
        }

        for (int i = 0, j = list.size() - 1; i < j; i++) {
            sb.append(list.get(i));
            sb.append(junctor);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }*/

    public static String createQuery(IFeedFilter iFeedFilter, Map<String, FieldInf> id2infs) {
        String query = "";
        Filter filter = iFeedFilter.getFilter();
        String filterQuery = iFeedFilter.getFilterQuery();

        if (filter == null || filter.getMetadataType() == null) {

            if (iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.validfrom")
                    || iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.availablefrom")) {
                query = iFeedFilter.getFilterKey() + ":[" + filterQuery + " TO *]";
            } else if (iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.validto")
                    || iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.availableto")) {
                query = iFeedFilter.getFilterKey() + ":[* TO " + filterQuery + "]";
            } else {
                //query = iFeedFilter.getFilterKey() + ":" + getAndFormatFilterQuery(iFeedFilter) + "";
                query = getAndFormatFilterQuery(iFeedFilter);
            }

        } else {
            switch (filter.getMetadataType()) {
                case TEXT_FREE:
                    // query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case TEXT_FIX:
                    // query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case LDAP_VALUE:
                    // query = filter.getFilterField() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case LDAP_ORG_VALUE:
                    // query = filter.getFilterField() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case DATE:
                    if (iFeedFilter.getOperator() == null || "matching".equals(iFeedFilter.getOperator())) {
                        query = toDateFilterValue(filter, filterQuery);
                    } else {
                        query = getAndFormatFilterQuery(iFeedFilter);
                    }
                    break;
                default:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
            }
        }
        return query;
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String getAndFormatFilterQuery(IFeedFilter iFeedFilter) {
        String ff = iFeedFilter.getFilterQuery();

        if (iFeedFilter.getFieldInf() != null && (iFeedFilter.getFieldInf().getType().equals("d:date")
                || iFeedFilter.getFieldInf().getType().equals("d:datetime"))
                && (ff.startsWith("+") || ff.startsWith("-")) && ff.length() > 1 && isDigit(ff.substring(1))
                ) {
            Date now = new Date();
            long daysOff = Integer.parseInt(ff.substring(1));
            if (ff.startsWith("-")) {
                daysOff = -daysOff;
            }
            daysOff = daysOff * 86400000;
            Date otherDay = new Date(now.getTime() + daysOff);
            ff = sdf.format(otherDay);
        }

        ff = SolrQueryEscaper.escape(ff);

        Filter filter = iFeedFilter.getFilter();
        String solrPropertyName;
        if (filter != null) {
            solrPropertyName = filter.getFilterField();
        } else {
            solrPropertyName = iFeedFilter.getFilterKey();
        }

        final String operator = iFeedFilter.getOperator();

        if (operator == null || "matching".equals(operator)) {
            ff = solrPropertyName + ":" + ff + "";
        } else {
            if (operator.equals("greater")) {
                ff = solrPropertyName + ":[" + ff + " TO *]";
            } else if (operator.equals("lesser")) {
                ff = solrPropertyName + ":[* TO " + ff + "]";
            }
        }
        return ff;
    }

    private static String toDateFilterValue(Filter filter, String filterQuery) {
        String query;
        if (filter.name().contains("FROM_DATE")) {
            query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
        } else if (filter.name().contains("TO_DATE")) {
            query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
        } else {
            throw new RuntimeException("Unable to build query. " + "Unknown filter date type found: "
                    + filter.name());
        }
        return query;
    }

}
