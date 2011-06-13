package se.vgregion.ifeed.intsvc.atom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

@Path("ifeeds")
public class IFeedFeedServiceImpl implements IFeedFeedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedFeedServiceImpl.class);

    private static final Map<String, String> NAMESPACES = new HashMap<String, String>();

    static {
        NAMESPACES.put("dc", "http://purl.org/dc/elements/1.1/");
        NAMESPACES.put("vgr", "http://purl.org/vgregion/elements/1.0/");
    }

    private IFeedService iFeedService;
    private IFeedSolrQuery solrQuery;

    @Context
    private UriInfo context;

    @Autowired
    public IFeedFeedServiceImpl(IFeedSolrQuery solrQuery, IFeedService iFeedService) {
        super();
        this.solrQuery = solrQuery;
        this.iFeedService = iFeedService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.ifeed.intsvc.atom.IFeedFeedService#getIFeed(java.lang.Long)
     */
    @Override
    @GET
    @Produces({ "application/xml", "application/atom+xml;type=feed;charset=UTF-8" })
    @Path("/{id}/feed")
    public Feed getIFeed(@PathParam("id") Long id) {
        Feed f = Abdera.getInstance().newFeed();

        // Retrieve feed from store
        IFeed retrievedFeed = iFeedService.getIFeed(id);

        // Throw 404 if the feed does not exist
        if (retrievedFeed == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
            // TODO create a html view of the error to enhance the browser experience
        }

        f.setTitle(retrievedFeed.getName());
        f.setUpdated(retrievedFeed.getTimestamp());
        f.setId(context.getAbsolutePath().toString());
        f.addLink(context.getAbsolutePath().toString(), "self");

        // Populate the feed with search results
        populateFeed(f, solrQuery.getIFeedResults(retrievedFeed));

        return f;
    }

    @GET
    @Produces({ "application/xml", "application/atom+xml;type=entry;charset=UTF-8" })
    @Path("/{id}")
    public Entry getIFeedEntry(@PathParam("id") Long id) {
        Entry e = Abdera.getInstance().newEntry();
        IFeed retrievedFeed = iFeedService.getIFeed(id);

        if (retrievedFeed == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
            // TODO create a html view of the error to enhance the browser experience
        }

        e.setTitle(retrievedFeed.getName());
        e.setUpdated(retrievedFeed.getTimestamp());
        e.setSummary(retrievedFeed.getDescription());
        e.setId(context.getAbsolutePath().toString());
        e.addAuthor(retrievedFeed.getUserId());
        e.addLink(context.getAbsolutePath().toString(), "self");

        StringBuilder content = new StringBuilder();
        content.append("<ul>");
        for (IFeedFilter iFeedFilter : retrievedFeed.getFilters()) {
            content.append("<li>").append(iFeedFilter.getFilter().getFilterField()).append(":").append(iFeedFilter.getFilterQuery()).append("</li>");
        }
        content.append("</ul>");

        e.setContentAsXhtml(content.toString());
        return e;
    }

    private Entry populateEntry(Entry e, Map<String, Object> map) {

        if (map.containsKey("dc.creator")) {
            e.addAuthor(map.get("dc.creator").toString());
        } else {
            e.addAuthor("- Author missing -");
        }

        if (map.containsKey("dc.title")) {
            e.setTitle(map.get("dc.title").toString());
        } else {
            e.setTitle("- Title missing -");
        }

        if (map.containsKey("processingtime")) {
            Date d = (Date) map.get("processingtime");
            e.setUpdated(d);
        } else {
            e.setUpdated(new Date(0L));
        }

        if (map.containsKey("url")) {
            String url = map.get("url").toString();
            url = url.replace(" ", "%20");
            e.addLink(url);
            e.setId(url);
            String mediatype = "application/octet-stream";
            if (map.containsKey("dc.format")) {
                mediatype = map.get("dc.format").toString();
            }
            e.setContent(new IRI(url), mediatype);
        }

        if (map.containsKey("dc.description")) {
            e.setSummary(map.get("dc.description").toString());
        }

        for (String fieldName : map.keySet()) {
            String[] parts = fieldName.split("\\.");
            if (parts.length > 1) {
                addElement(e, parts[0], fieldName, map.get(fieldName));
            }
        }
        return e;
    }

    private DateFormat getSolrDateFormat() {
        final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";
        final String ZULU_TIMEZONE = "Zulu";

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        TimeZone zuluTimeZone = TimeZone.getTimeZone(ZULU_TIMEZONE);
        dateFormat.setTimeZone(zuluTimeZone);

        return dateFormat;
    }

    private void addElement(Entry e, String prefix, String fieldName, Object fieldValue) {
        // TODO Should handle the collection properly rather than just doing toString
        Element element = e.addExtension(new QName(NAMESPACES.get(prefix), fieldName.substring(prefix.length() + 1), prefix));
        if (fieldValue instanceof Date) {
            element.setText(getSolrDateFormat().format((Date) fieldValue));
        } else if (fieldName.equalsIgnoreCase("dc.language")) {
            if (fieldValue.toString().equalsIgnoreCase("svenska")) {
                element.setText("swe");
            } else if (fieldValue.toString().equalsIgnoreCase("svenska")) {
                element.setText("eng");
            } else {
                element.setText(fieldValue.toString());
            }
        } else {
            element.setText(fieldValue.toString());
        }
    }

    private Feed populateFeed(Feed f, Collection<Map<String, Object>> hits) {
        for (Map<String, Object> map : hits) {
            Entry e = f.addEntry();
            populateEntry(e, map);
        }
        return f;
    }
}