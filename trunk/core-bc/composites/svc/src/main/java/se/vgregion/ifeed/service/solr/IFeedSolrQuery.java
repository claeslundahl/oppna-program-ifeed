package se.vgregion.ifeed.service.solr;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedSolrQuery.class);

    private CommonsHttpSolrServer solrServer;

    /*public enum ORDER { desc, asc;
		public ORDER reverse() {
			return (this == asc) ? desc : asc;
		}
	}*/


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
            hits = (ArrayList<Map<String, Object>>)this.query().getResults().clone();
        } catch (MalformedURLException e) {
            LOGGER.error("Felaktigt url till sökserver: {}", e.getCause());
        } catch (SolrServerException e) {
            LOGGER.error("Serverfel: {}", e.getCause());
        }
        return hits;
    }

    public List<Map<String, Object>> getIFeedResults(IFeed iFeed) {
        setQuery("");
        String[] solrFilters = new String[iFeed.getFilters().size()];

        int index = 0;
        for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
            solrFilters[index++] = iFeedFilter.getFilter().getFilterField() + ":" + iFeedFilter.getFilterQuery();
        }
        // Populate the query with the feed's filters
        setFilterQueries(solrFilters);

        // Perform query
        List<Map<String, Object>> hits = doFilterQuery();
        LOGGER.debug("Number of search hits: {}", hits.size());

        return hits;
    }
}
