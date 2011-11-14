package se.vgregion.ifeed.service.solr;

import static org.apache.commons.lang.StringUtils.*;
import static se.vgregion.common.utils.CommonUtils.*;
import static se.vgregion.ifeed.service.solr.DateFormatter.DateFormat.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.common.utils.CommonUtils;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

public class IFeedSolrQuery extends SolrQuery {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedSolrQuery.class);

    public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.desc;
    public static final String DEFAULT_SORT_FIELD = "dc.title";

    private SolrServer solrServer;

    public IFeedSolrQuery(SolrServer solrServer) {
        this.solrServer = solrServer;
        this.setFields("*");
    }

    public enum SortDirection {
        desc, asc;
        public SortDirection reverse() {
            return (this == asc) ? desc : asc;
        }
    }

    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> doFilterQuery(final String sortField,
            final SortDirection sortDirection) {
        this.setSortField(sortField, ORDER.valueOf(sortDirection.name()
                .toLowerCase(CommonUtils.SWEDISH_LOCALE)));

        List<Map<String, Object>> hits = Collections.emptyList();
        try {
            QueryResponse response = solrServer.query(this);
            hits = (ArrayList<Map<String, Object>>)
                    response.getResults().clone();
        } catch (SolrServerException e) {
            LOGGER.error("Serverfel: {}", e.getCause());
        }
        return hits;
    }

    private void addOffsetFilter(Date offset) {
        if (offset != null) {
            LOGGER.debug("Searching for new document since: {}",
                    "processingtime:[" + DateFormatter.format(
                            offset, SOLR_DATE_FORMAT) + " TO NOW]");
            addFilterQuery("processingtime:[" + DateFormatter.format(
                    offset, SOLR_DATE_FORMAT) + " TO NOW]");
            LOGGER.debug("Add offset filter {}",
                    Arrays.toString(getFilterQueries()));

        }
    }

    private void addUnPublishedFilter() {
        addFilterQuery("published:true");
        LOGGER.debug("Add unpublished filter {}",
                Arrays.toString(getFilterQueries()));
    }

    private void addFeedFilters(IFeed iFeed) {
        // Populate the query with the feed's filters
        for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
            addFilterQuery(SolrQueryBuilder.createQuery(iFeedFilter));
        }
        LOGGER.debug("Add Feed Filters: {}",
                Arrays.toString(getFilterQueries()));
    }

    public List<Map<String, Object>> getIFeedResults(IFeed iFeed) {
        addFeedFilters(iFeed);
        addUnPublishedFilter();

        SortDirection direction = isBlank(iFeed.getSortDirection()) ?
                DEFAULT_SORT_DIRECTION : SortDirection.valueOf(
                        iFeed.getSortDirection());
        return prepareAndPerformQuery(iFeed.getSortField(), direction);
    }

    public List<Map<String, Object>> getIFeedResults(IFeed iFeed, Date offset) {
        addFeedFilters(iFeed);
        addOffsetFilter(offset);

        return prepareAndPerformQuery(
                DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
    }

    public List<Map<String, Object>> getIFeedResults(IFeed iFeed, String sortField, SortDirection sortDirection) {
        addFeedFilters(iFeed);
        addUnPublishedFilter();

        return prepareAndPerformQuery(sortField, sortDirection);
    }

    private List<Map<String, Object>> prepareAndPerformQuery(
            final String sortField, final SortDirection sortDirection) {
        LOGGER.debug(
                "Search filters: {}", Arrays.toString(this.getFilterQueries()));

        String sortBy = isBlank(sortField) ? DEFAULT_SORT_FIELD : sortField;
        SortDirection direction = isNull(sortDirection) ?
                DEFAULT_SORT_DIRECTION : sortDirection;
        LOGGER.debug("Sort by: {}, Order: {}",
                new Object[] {sortBy, direction});

        setQuery("");

        // Perform query
        List<Map<String, Object>> hits = doFilterQuery(sortBy, direction);
        LOGGER.debug("Number of search hits: {}", hits.size());

        // Clear filter queries for next query
        setFilterQueries(new String[0]);
        return hits;
    }
}
