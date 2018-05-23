package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.StringUtils;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class SolrQueryBuilder {

    public static String createOrQuery(Collection<IFeedFilter> filters) {
        if (filters.isEmpty()) {
            throw new RuntimeException("No filter provided");
        }
        if (filters.size() == 1) {
            return createQuery(filters.iterator().next(), getFieldInfMap());
        }
        List<String> parts = new ArrayList<String>();
        for (IFeedFilter filter : filters) {
            parts.add(createQuery(filter, getFieldInfMap()));
        }
        return "((" + se.vgregion.ifeed.service.ifeed.Filter.join(parts, ") OR (") + "))";
    }

    public static String createQuery(IFeedFilter iFeedFilter, Map<String, FieldInf> id2infs) {
        if (id2infs == null) {
            id2infs = getFieldInfMap();
        }
        String query = "";
        Filter filter = iFeedFilter.getFilter();
        String filterQuery = iFeedFilter.getFilterQuery();

        if (iFeedFilter.getFieldInf() == null) {
            String key = iFeedFilter.getFilterKey();
            iFeedFilter.setFieldInf(id2infs.get(key));
        }

        if (filter == null || filter.getMetadataType() == null) {
            query = getAndFormatFilterQuery(iFeedFilter);
        } else {
            switch (filter.getMetadataType()) {
                case TEXT_FREE:
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case TEXT_FIX:
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case LDAP_VALUE:
                    query = getAndFormatFilterQuery(iFeedFilter);
                    break;
                case LDAP_ORG_VALUE:
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

    private static boolean isSomeKindOfDate(IFeedFilter iFeedFilter) {

        if (iFeedFilter.getFieldInf() == null || iFeedFilter.getFieldInf().getType() == null) {
            return false;
        }
        return iFeedFilter.getFieldInf() != null && (iFeedFilter.getFieldInf().getType().equals("d:date")
                || iFeedFilter.getFieldInf().getType().equals("d:datetime"));
    }

    private static boolean isOfMatchingType(IFeedFilter iFeedFilter) {
        String operator = iFeedFilter.getOperator();
        return "matching".equalsIgnoreCase(operator) || StringUtils.isEmpty(operator);
    }

    private static String getAndFormatFilterQuery(IFeedFilter iFeedFilter) {
        String ff = iFeedFilter.getFilterQuery();

        if (isSomeKindOfDate(iFeedFilter)) {
            if (ff.startsWith("+") || ff.startsWith("-") && ff.length() > 1 && isDigit(ff.substring(1))) {
                Date now = new Date();
                long daysOff = Integer.parseInt(ff.substring(1));
                if (ff.startsWith("-")) {
                    daysOff = -daysOff;
                }
                daysOff = daysOff * 86400000;
                Date otherDay = new Date(now.getTime() + daysOff);
                ff = sdf.format(otherDay);
            }
        }

        ff = SolrQueryEscaper.escape(ff);

        Filter filter = iFeedFilter.getFilter();
        String solrPropertyName;
        if (filter != null) {
            solrPropertyName = filter.getFilterField();
        } else {
            solrPropertyName = iFeedFilter.getFilterKey();
        }

        if (isOfMatchingType(iFeedFilter)) {
            if (isSomeKindOfDate(iFeedFilter)) {
                ff = solrPropertyName + ":" + ff + "*";
            } else {
                ff = solrPropertyName + ":" + ff + "";
            }
        } else {
            final String operator = iFeedFilter.getOperator();
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

    private static IFeedService iFeedService;

    public static Map<String, FieldInf> getFieldInfMap() {
        if (iFeedService == null) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "fields.conf"));
                FieldsInf fi = new FieldsInf();
                fi.setText(new String(bytes));
                Map<String, FieldInf> map = new HashMap<>();
                for (FieldInf field : fi.getFieldInfs()) {
                    map.put(field.getId(), field);
                }
                return map;
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
        return iFeedService.mapFieldInfToId();
    }

    public static void setIFeedService(IFeedService iFeedService) {
        SolrQueryBuilder.iFeedService = iFeedService;
    }

    public static IFeedService getIFeedService() {
        return iFeedService;
    }
}
