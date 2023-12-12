package se.vgregion.ifeed.service.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.common.utils.CommonUtils;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.text.Collator;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.join;
import static se.vgregion.common.utils.CommonUtils.isNull;
import static se.vgregion.ifeed.service.solr.DateFormatter.DateFormat.SOLR_DATE_FORMAT;
import static se.vgregion.ifeed.service.solr.SolrFacetUtil.getFirstNonBlank;

public class IFeedSolrQuery extends SolrQuery {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedSolrQuery.class);

    public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.desc;
    //public static final String DEFAULT_SORT_FIELD = "dc.title";
    public static final String DEFAULT_SORT_FIELD = "title";
    public static final int DEFAULT_SOLR_RESULT_SIZE = 1_000_000;

    private static final SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    private IFeedService iFeedService;

    private SolrServer solrServer;

    public IFeedSolrQuery(SolrServer solrServer, IFeedService iFeedService) {
        this.solrServer = solrServer;
        this.iFeedService = iFeedService;
        //this.setFields("*");
    }

    public enum SortDirection {
        desc, asc;

        public SortDirection reverse() {
            return (this == asc) ? desc : asc;
        }
    }

    static Collator getSwedishComparator() {
        final Collator collator = new SwedishCollator(Collator.getInstance(new Locale("sv", "SE")));
        collator.setStrength(Collator.PRIMARY);
        return collator;
    }

    static void sort(final List<Map<String, Object>> list, final String byThisField, final int inDirection) {
        final Collator collator = getSwedishComparator();
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                Object o1 = map1.get(byThisField);
                Object o2 = map2.get(byThisField);

                if (o1 == o2) {
                    return 0;
                }

                if (o1 instanceof String && o2 instanceof String) {
                    return inDirection
                            * collator.compare(((String) o1).trim().toLowerCase(), ((String) o2).trim().toLowerCase());
                }

                if (o1 instanceof Comparable && o2 instanceof Comparable) {
                    return ((Comparable) o1).compareTo(o2) * inDirection;
                }

                if (o1 == null) {
                    return -1 * inDirection;
                }

                if (o2 == null) {
                    return +1 * inDirection;
                }

                return collator.compare(o1, o2) * inDirection;
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> doFilterQuery(final String sortField, final SortDirection sortDirection, final String[] fieldsToSelect) {
        List<Map<String, Object>> result = doFilterQueryImpl(sortField, sortDirection, fieldsToSelect);
        return result;
    }

    protected List<Map<String, Object>> doFilterQueryImpl(final String sortField, final SortDirection sortDirection, final String[] fieldsToSelect) {
        this.setSortField(sortField, ORDER.valueOf(sortDirection.name().toLowerCase(CommonUtils.SWEDISH_LOCALE)));

        List<Map<String, Object>> hits = Collections.emptyList();
        if (this.getFilterQueries().length > 0) {
            if (fieldsToSelect != null && fieldsToSelect.length > 0) {
                HashSet<String> set = new HashSet<String>(Arrays.asList(fieldsToSelect));
                set.addAll(Arrays.asList("DC.publisher.forunit", "DC.description", "DC.creator.document", "dc.identifier.documentid",
                        "DC.creator.function", "DC.contributor.acceptedby", "id",
                        "DC.contributor.acceptedby.role", "DC.date.validfrom", "DC.date.validto", "DC.type.document.structure",
                        //"dc.title", "dc.identifier.native", "url"));
                        "title", "dc.identifier.native", "url"));
                List<String> result = new ArrayList<>();
                for (String s : set) {
                    result.add(s.toLowerCase());
                }
                for (String s : result) {
                    addField(s);
                }
            } else {
                setFields("*");
            }
            for (int i = 0; i < 3; i++) {
                try {
                    setQuery("*:*");
                    QueryResponse response = solrServer.query(this);
                    //Map map = new HashMap(response.getDebugMap());
                    //LOGGER.error(map.toString());
                    // Uncomment for debug info.

                    SolrDocumentList sdl = response.getResults();
                    hits = (ArrayList<Map<String, Object>>) sdl.clone();

                    final int direction = sortDirection.equals(SortDirection.asc) ? 1 : -1;
                    sort(hits, sortField, direction);
                    return hits;
                } catch (SolrServerException e) {
                    e.printStackTrace();
                    LOGGER.error("Serverfel: {}", e.getCause());
                    LOGGER.error("Trying again, This where the " + i + "nt time of 3 (0-2).");
                    LOGGER.error("latestArg2getIFeedResults = " + latestArg2getIFeedResults);
                }
            }
        }
        return hits;
    }

    static String formatTextBeforeSorting(Object value) {
        if (value instanceof Date) {
            return String.valueOf(((Date) value).getTime());
        }
        return padTrailing(String.valueOf(value), 13, '9');
    }

    private static String padTrailing(String forThat, int upToPosition, Character with) {
        for (int i = forThat.length(); i < upToPosition; i++) {
            forThat += with;
        }
        return forThat;
    }

    private void addOffsetFilter(Date offset) {
        if (offset != null) {
            LOGGER.debug("Searching for new document since: {}",
                    "processingtime:[" + DateFormatter.format(offset, SOLR_DATE_FORMAT) + " TO NOW]");
            addFilterQuery("processingtime:[" + DateFormatter.format(offset, SOLR_DATE_FORMAT) + " TO NOW]");
            LOGGER.debug("Add offset filter {}", Arrays.toString(getFilterQueries()));
        }
    }

    private void addUnPublishedFilter() {
        addFilterQuery("published:true");
        LOGGER.debug("Add unpublished filter {}", Arrays.toString(getFilterQueries()));
    }

    private void addFeedFilters(IFeed iFeed) {
        List<String> resultWithOrBetween = new ArrayList<String>();
        addFeedFiltersImpl(iFeed, resultWithOrBetween, new HashSet<IFeed>());

        Iterator<String> i = resultWithOrBetween.iterator();
        while (i.hasNext()) {
            String s = i.next();
            if (s == null || s.trim().isEmpty()) {
                i.remove();
            }
        }

        if (resultWithOrBetween.isEmpty()) {
            // Do nothing.
        } else {
            if (resultWithOrBetween.size() == 1) {
                addFilterQuery(resultWithOrBetween.get(0));
            } else {
                addFilterQuery("((" + Filter.join(resultWithOrBetween, ") OR (") + "))");
            }
        }
    }

    private void addFeedFiltersImpl(IFeed iFeed, List<String> resultWithOrBetween, Set<IFeed> handled) {
        if (handled.contains(iFeed)) {
            return; // Avoids infinite recursive calls.
        }
        handled.add(iFeed);

        FeedFilterBag bag = new FeedFilterBag();

        for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
            bag.get(iFeedFilter.getFilterKey() + getFirstNonBlank(iFeedFilter.getOperator(), "matching")).add(iFeedFilter);
            //bag.get(iFeedFilter.getFilterKey()).add(iFeedFilter);
        }

        List<String> queryParts = new ArrayList<String>();

        for (String key : bag.keySet()) {
            List<IFeedFilter> filters = bag.get(key);
            if (filters.size() == 1) {
                queryParts.add(SolrQueryBuilder.createQuery(filters.get(0), iFeedService.mapFieldInfToId()));
            } else {
                if (filters.size() == 0)
                    throw new RuntimeException("Here the (()) might occur!");
                String fq = SolrQueryBuilder.createOrQuery(filters);
                queryParts.add(fq);
            }
        }
        resultWithOrBetween.add(join(queryParts, " AND "));

        for (IFeed composite : iFeed.getComposites()) {
            addFeedFiltersImpl(composite, resultWithOrBetween, handled);
        }

        LOGGER.debug("Add Feed Filters: {}", Arrays.toString(getFilterQueries()));
    }

    public IFeedResults getIFeedResults(IFeed iFeed, final String[] fieldsToSelect) {
        addFeedFilters(iFeed);
        addUnPublishedFilter();

        SortDirection direction = isBlank(iFeed.getSortDirection()) ? DEFAULT_SORT_DIRECTION : SortDirection
                .valueOf(iFeed.getSortDirection());

        IFeedResults results = new IFeedResults();
        results.setQueryUrl(ClientUtils.toQueryString(this, false));

        results.addAll(prepareAndPerformQuery(getFieldInf(iFeed.getSortField()), direction, iFeed, fieldsToSelect));

        return results;
    }

    private ThreadLocal<IFeed> latestArg2getIFeedResults = new ThreadLocal<IFeed>();
    // Used for debugging.

    public List<Map<String, Object>> getIFeedResults(IFeed iFeed, Date offset, final String[] fieldsToSelect) {
        latestArg2getIFeedResults.set(iFeed);
        addFeedFilters(iFeed);
        addOffsetFilter(offset);

        FieldInf defaultField = getFieldInf(DEFAULT_SORT_FIELD);

        List<Map<String, Object>> result = prepareAndPerformQuery(defaultField, DEFAULT_SORT_DIRECTION, iFeed, fieldsToSelect);

        return result;
    }

    protected FieldInf getFieldInf(String byId) {
        FieldInf defaultField = iFeedService.getFieldInf(byId);
        return defaultField;
    }

    public List<Map<String, Object>> getIFeedResults(IFeed iFeed, FieldInf sortField, SortDirection sortDirection, final String[] fieldsToSelect) {
        latestArg2getIFeedResults.set(iFeed);
        addFeedFilters(iFeed);
        addUnPublishedFilter();
        return prepareAndPerformQuery(sortField, sortDirection, iFeed, fieldsToSelect);
    }

    private List<Map<String, Object>> prepareAndPerformQuery(FieldInf fieldInf,
                                                             final SortDirection sortDirection, IFeed feed,
                                                             final String[] fieldsToSelect) {

        String sortField = fieldInf == null ? null : fieldInf.getId();
        long before = System.currentTimeMillis();
        // List<Map<String, Object>> result = prepareAndPerformQueryImpl(sortField, sortDirection, fieldsToSelect);
        LOGGER.debug("Time for solr " + (System.currentTimeMillis() - before) + " for id " + (feed != null ? feed.getId() : "null"));
        if (sortField == null || sortField.trim().isEmpty() || "null".equalsIgnoreCase(sortField)) {
            //sortField = "dc.title";
            sortField = "title";
        }
        Result someOtherResult = client.query(feed.toQuery(client.fetchFields()), 0, DEFAULT_SOLR_RESULT_SIZE, sortDirection.name(), fieldInf);
        // if (sortField.equals("dc.title") || sortField.equals("title")) {
        SortingMap sortingMap = new SortingMap();
        for (Map<String, Object> item : someOtherResult.getDocumentList().getDocuments()) {
            sortingMap.get(
                    item.get(sortField))
                    .add(item);
        }
        List<Map<String, Object>> result = sortingMap.toFlatList();

        if ("desc".equalsIgnoreCase(sortDirection.name())) {
            Collections.reverse(result);
        }

        return result;
        // }

        // System.out.println("Diff on results " + (someOtherResult.getResponse().getDocs().size() + " == " + result.size()));
        //  return someOtherResult.getResponse().getDocs();
        // return result;
    }

    private List<Map<String, Object>> prepareAndPerformQueryImpl(final String sortField,
                                                                 final SortDirection sortDirection,
                                                                 final String[] fieldsToSelect) {
        LOGGER.debug("Search filters: {}", Arrays.toString(this.getFilterQueries()));

        String sortBy = isBlank(sortField) ? DEFAULT_SORT_FIELD : sortField;
        SortDirection direction = isNull(sortDirection) ? DEFAULT_SORT_DIRECTION : sortDirection;
        LOGGER.debug("Sort by: {}, Order: {}", new Object[]{sortBy, direction});

        // Perform query
        List<Map<String, Object>> hits = doFilterQuery(sortBy, direction, fieldsToSelect);
        LOGGER.debug("Number of search hits: {}", hits.size());

        // Clear filter queries for next query
        setFilterQueries(new String[0]);
        return hits;
    }

    public static class FeedFilterBag extends HashMap<String, List<IFeedFilter>> {
        @Override
        public List<IFeedFilter> get(Object key) {
            List<IFeedFilter> result = super.get(key);    //To change body of overridden methods use File | Settings | File Templates.
            if (result == null) {
                put((String) key, result = new ArrayList<IFeedFilter>());
            }
            return result;
        }
    }

    private static class SortingMap extends TreeMap<String, List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> get(Object key) {
            if (key == null) {
                key = "";
            } else if (!(key instanceof String)) {
                key = key.toString();
            }
            String name = (String) key;
            name = name.trim().toLowerCase();
            if (!containsKey(key)) {
                put(name, new ArrayList<>());
            }
            return super.get(name);
        }

        List<Map<String, Object>> toFlatList() {
            List<Map<String, Object>> result = new ArrayList<>();

            for (String key : keySet()) {
                result.addAll(get(key));
            }

            return result;
        }

    }

}
