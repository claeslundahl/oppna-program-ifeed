package se.vgregion.ifeed.service.solr;

import static org.apache.commons.lang.StringUtils.*;
import static se.vgregion.common.utils.CommonUtils.*;
import static se.vgregion.ifeed.service.solr.DateFormatter.DateFormats.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

public class IFeedSolrQuery extends SolrQuery {

    public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.desc;
    public static final String DEFAULT_SORT_FIELD = "processingtime";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedSolrQuery.class);

    private CommonsHttpSolrServer solrServer;

    public enum SortDirection {
        desc, asc;
        public SortDirection reverse() {
            return (this == asc) ? desc : asc;
        }
    }

    public void setSolrServer(CommonsHttpSolrServer solrServer) {
        this.solrServer = solrServer;
        this.solrServer.setRequestWriter(new BinaryRequestWriter());
    }

    public CommonsHttpSolrServer getSolrServer() {
        return solrServer;
    }

    protected QueryResponse query() throws MalformedURLException, SolrServerException {
        this.setFields("*");
        this.setRows(100);
        return getSolrServer().query(this);
    }

    protected List<Map<String, Object>> doFilterQuery(String sortField, SortDirection sortDirection) {
        this.setSortField(sortField, ORDER.valueOf(sortDirection.name().toLowerCase()));
        List<Map<String, Object>> hits = Collections.emptyList();
        try {
            QueryResponse response = this.query();
            hits = (ArrayList<Map<String, Object>>) response.getResults().clone();
        } catch (MalformedURLException e) {
            LOGGER.error("Felaktigt url till sökserver: {}", e.getCause());
        } catch (SolrServerException e) {
            LOGGER.error("Serverfel: {}", e.getCause());
        }
        return hits;
    }

    public Collection<Map<String, Object>> getIFeedResults(IFeed iFeed, Date offset) {
        // Adding date offset to filter
        if (offset != null) {
            LOGGER.debug("Searching for new document since: {}",
                    "processingtime:[" + DateFormatter.format(offset, SOLR_DATE_FORMAT) + " TO NOW]");
            addFilterQuery("processingtime:[" + DateFormatter.format(offset, SOLR_DATE_FORMAT) + " TO NOW]");
        }
        return getIFeedResults(iFeed, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
    }

    public Collection<Map<String, Object>> getIFeedResults(IFeed iFeed, String sortField, SortDirection sortDirection) {
        this.setShowDebugInfo(true);
        setQuery("");

        // Populate the query with the feed's filters
        for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
            addFilterQuery(SolrQueryBuilder.createQuery(iFeedFilter));
        }

        LOGGER.debug("Search filters: {}", Arrays.toString(this.getFilterQueries()));
        // Perform query
        String sortBy = isBlank(sortField) ? DEFAULT_SORT_FIELD : sortField;
        SortDirection direction = isNull(sortDirection) ? DEFAULT_SORT_DIRECTION : sortDirection;
        LOGGER.debug("Sort by: {}, Order: {}", new Object[] { sortBy, direction });
        List<Map<String, Object>> hits = doFilterQuery(sortBy, direction);
        LOGGER.debug("Number of search hits: {}", hits.size());

        // Clear filter queries for next query
        setFilterQueries(new String[0]);

        return hits;
    }

    public Collection<Map<String, Object>> getIFeedResults(IFeed iFeed) {
        return getIFeedResults(iFeed, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
    }
}
