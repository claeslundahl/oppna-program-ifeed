package se.vgregion.ifeed.service.solr;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;


public class IFeedSolrQuery extends SolrQuery {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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
    public ArrayList<Map<String, Object>> doFilterQuery() {
        this.setSortField("revisiondate", SolrQuery.ORDER.desc);
        ArrayList<Map<String, Object>> hits = null;
        try {
            hits = (ArrayList<Map<String, Object>>)this.query().getResults().clone();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        return doFilterQuery();
    }
}
