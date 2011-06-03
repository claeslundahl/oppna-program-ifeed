package se.vgregion.ifeed.service.solr;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedSolrQuery.class);

    private CommonsHttpSolrServer solrServer;

    /*
     * public enum ORDER { desc, asc; public ORDER reverse() { return (this == asc) ? desc : asc; } }
     */

    public void setSolrServer(CommonsHttpSolrServer solrServer) {
        this.solrServer = solrServer;
        this.solrServer.setRequestWriter(new BinaryRequestWriter());
    }

    public CommonsHttpSolrServer getSolrServer() {
        return solrServer;
    }

    public QueryResponse query() throws MalformedURLException, SolrServerException {
        return getSolrServer().query(this);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> doFilterQuery() {
        this.setSortField("revisiondate", SolrQuery.ORDER.desc);
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
                    "processingtime:[" + getSolrDateFormat().format(offset) + " TO NOW]");
            addFilterQuery("processingtime:[" + getSolrDateFormat().format(offset) + " TO NOW]");
        }
        return getIFeedResults(iFeed);
    }

    private DateFormat getSolrDateFormat() {
        final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";
        final String ZULU_TIMEZONE = "Zulu";

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        TimeZone zuluTimeZoneori = TimeZone.getTimeZone(ZULU_TIMEZONE);
        dateFormat.setTimeZone(zuluTimeZoneori);

        return dateFormat;
    }

    public Collection<Map<String, Object>> getIFeedResults(IFeed iFeed) {
        this.setShowDebugInfo(true);

        setQuery("");

        // Populate the query with the feed's filters
        for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
            addFilterQuery(iFeedFilter.getFilter().getFilterField() + ":" + iFeedFilter.getFilterQuery() + "*");
        }

        LOGGER.debug("Search filters: {}", Arrays.toString(this.getFilterQueries()));
        // Perform query
        List<Map<String, Object>> hits = doFilterQuery();
        LOGGER.debug("Number of search hits: {}", hits.size());

        setFilterQueries(new String[0]);

        return hits;
    }

    public static void main(String[] args) {
        final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";
        final String ZULU_TIMEZONE = "Zulu";

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        TimeZone zuluTimeZoneori = TimeZone.getTimeZone(ZULU_TIMEZONE);
        dateFormat.setTimeZone(zuluTimeZoneori);

        // get current Date/time
        Date now = new Date();
        String dateTimestampFormat = dateFormat.format(now); // NOTE: pay attention to this line.

        System.out.println(dateTimestampFormat);
    }

}
