package se.vgregion.ifeed.intsvc.atom;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

@Path("ifeed")
public class IFeedFeedServiceImpl implements IFeedFeedService {

    private IFeedSolrQuery solrQuery;
    private IFeedService iFeedService;

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.ifeed.intsvc.atom.IFeedFeedService#getIFeed(java.lang.Long)
     */
    @Override
    @GET
    @Produces({ "application/xml", "application/atom+xml;type=feed" })
    @Path("/feed/{id}")
    public Feed getIFeed(@PathParam("id") Long id) {
        Feed f = Abdera.getInstance().newFeed();

        // f.setIcon("http://vard.vgregion.se/favicon.ico");
        // f.setUpdated(value);
        // f.setGenerator(iri, version, value);

        //solrQuery.setQuery("").setRows(20);

        // Retrieve feed from store
        IFeed retrievedFeed = iFeedService.getIFeed(id);

        // Throw 404 if the feed does not exist
        if (retrievedFeed == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
            // TODO create a html view of the error to enhance the browser experience
        }

        f.setTitleAsXhtml(retrievedFeed.getName());
        f.setUpdated(retrievedFeed.getTimestamp());

        // Populate the query with the feed's filters
        // solrQuery.setFilterQueries(retrievedFeed.getFilters().toArray(new String[0]));

        // Perform query
        // List<Map<String, Object>> hits = solrQuery.doFilterQuery();
        
        // Populate the feed with search results
        populateFeed(f, solrQuery.getIFeedResults(retrievedFeed));

        return f;
    }

    /*
     * @GET
     * 
     * @Produces({ "application/xml", "application/atom+xml;type=entry" })
     * 
     * @Path("/entry/{id}") public Entry getIFeedEntry(@PathParam("id") Long id) { Entry e =
     * Abdera.getInstance().newEntry();
     * 
     * // Populate the entry with some data
     * 
     * return e; }
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * se.vgregion.ifeed.intsvc.atom.IFeedFeedService#setSolrQuery(se.vgregion.ifeed.service.solr.IFeedSolrQuery)
     */
    @Override
    public void setSolrQuery(IFeedSolrQuery solrQuery) {
        this.solrQuery = solrQuery;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.ifeed.intsvc.atom.IFeedFeedService#getSolrQuery()
     */
    @Override
    public IFeedSolrQuery getSolrQuery() {
        return solrQuery;
    }

    public IFeedService getFeedService() {
        return iFeedService;
    }

    public void setFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    private Entry populateEntry(Entry e, Map<String, Object> map) {
        DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss zzzz yyyy");
        try {
            e.setUpdated(df.parse(map.get("revisiondate").toString()));
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        map.remove("revisiondate");

        if (map.get("author") != null) {
            e.addAuthor(map.get("author").toString());
            map.remove("author");
        }

        e.setTitleAsXhtml(map.get("title").toString());
        map.remove("title");

        String url = map.get("url").toString().replace(" ", "%20");
        e.addLink(url);
        e.setId(url);
        map.remove("url");

        map.remove("body");

//        for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
//            String k = mapEntry.getKey();
//            // TODO Should handle the collection properly rather than just doing toString
//            String v = mapEntry.getValue().toString();
//            QName extensionQName = new QName("tag:vgregion.se,2006:/namespaces", k, "vgr");
//            Element element = e.addExtension(extensionQName);
//            element.setText(v);
//        }
        return e;
    }

    private Feed populateFeed(Feed f, List<Map<String, Object>> hits) {
        for (Map<String, Object> map : hits) {
            Entry e = f.addEntry();
            populateEntry(e, map);
        }
        return f;
    }
}