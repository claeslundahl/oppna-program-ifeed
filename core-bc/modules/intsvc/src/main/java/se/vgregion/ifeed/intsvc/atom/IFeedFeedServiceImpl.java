package se.vgregion.ifeed.intsvc.atom;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.DateFormatter.DateFormat;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.service.solr.SolrServerFactory;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static se.vgregion.common.utils.CommonUtils.getEnum;

@Path("documentlists")
public class IFeedFeedServiceImpl implements IFeedFeedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedFeedServiceImpl.class);

    private Map<String, String> namespaces = new HashMap<String, String>(getInitialNamespaces());

    private Map<String, String> getInitialNamespaces() {
        Map<String, String> result = new HashMap<>();
        result.put("core:ArchivalObject", "http://vgregion.se");
        result.put("vgr:VgrExtension", "http://vgregion.se");
        result.put("vgrsd:DomainExtension", "http://vgregion.se");
        result.put("vgrsy:DomainExtension", "http://vgregion.se");
        return result;
    }

    private String pushServerEndpoint = null;

    private IFeedService iFeedService;
    private IFeedSolrQuery solrQuery;

    @Context
    private UriInfo context;
    private SolrServer solrServer;

    @Autowired
    public IFeedFeedServiceImpl(IFeedSolrQuery solrQuery, IFeedService iFeedService) {
        super();
        this.solrQuery = solrQuery;
        this.iFeedService = iFeedService;
    }

    public void setPushServerEndpoint(String pushServerEndpoint) {
        this.pushServerEndpoint = pushServerEndpoint;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }


    private SolrServer getOrCreateSolrQuery() {
        if (solrServer == null) {
            solrServer = SolrServerFactory.create();
        }
        return solrServer;
    }

    /* (non-Javadoc)
     *
     * @see se.vgregion.ifeed.intsvc.atom.IFeedFeedService#getIFeed(java.lang.Long) */
    @Override
    @GET
    @Produces({"application/atom+xml", "application/atom+xml;type=feed;charset=UTF-8"})
//    @Produces({"application/xml", "application/atom+xml;type=feed;charset=UTF-8"})
    @Path("/{id}/feed")
    public Feed getIFeed(@PathParam("id") Long id, @QueryParam("by") String sortField,
                         @QueryParam("dir") String sortDirection) {

        solrQuery = new IFeedSolrQuery(getOrCreateSolrQuery(), iFeedService);

        Feed f = Abdera.getInstance().newFeed();

        // Retrieve feed from store
        IFeed retrievedFeed = iFeedService.getIFeed(id);

        // Throw 404 if the feed doesn't exist
        if (retrievedFeed == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
            // TODO create a html view of the error to
            // enhance the browser experience
        }

        f.setTitle(retrievedFeed.getName());
        f.setUpdated(retrievedFeed.getTimestamp());
        f.setId(context.getAbsolutePath().toString());
        f.addLink(context.getAbsolutePath().toString(), "self");
        f.addLink(pushServerEndpoint, "hub");

        // Populate the feed with search results
        populateFeed(f,
                solrQuery.getIFeedResults(retrievedFeed, iFeedService.getFieldInf(sortField), getEnum(SortDirection.class, sortDirection), null));

        solrQuery.clear();

        return f;
    }

    @Override
    @GET
    @Produces({"application/xml", "application/atom+xml;type=entry;charset=UTF-8"})
    @Path("/{id}/metadata")
    public Entry getIFeedEntry(@PathParam("id") final Long id) {
        Entry e = Abdera.getInstance().newEntry();
        IFeed retrievedFeed = iFeedService.getIFeed(id);

        if (retrievedFeed == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
            // TODO create a html view of the error to
            // enhance the browser experience
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
            content.append("<li>").append(iFeedFilter.getFilter().getFilterField()).append(":")
                    .append(iFeedFilter.getFilterQuery()).append("</li>");
        }
        content.append("</ul>");

        e.setContentAsXhtml(content.toString());
        return e;
    }


    Entry populateEntry(final Entry e, final Map<String, Object> map) {

        if (map.containsKey("dc.contributor.savedby")) {
            e.addAuthor(map.get("dc.contributor.savedby").toString());
        } else if (map.containsKey("vgr:VgrExtension.vgr:CreatedBy")) {
            e.addAuthor(map.get("vgr:VgrExtension.vgr:CreatedBy").toString());
        } else {
            e.addAuthor("- Author missing -");
        }

        if (map.containsKey("title")) {
            e.setTitle(map.get("title").toString());
        } else {
            e.setTitle("- Title missing -");
        }
        if (map.containsKey("dc.date.issued")) {
            String dcDateIssued = (String) map.get("dc.date.issued");
            e.setUpdated(DateFormatter.parse(dcDateIssued));
        } else if (map.containsKey("processingtime")) {
            if (map.get("processingtime") instanceof String) {
                e.setUpdated(toUtcDateIfPossible((String) map.get("processingtime")));
            } else {
                Date d = (Date) map.get("processingtime");
                e.setUpdated(d);
            }
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

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            String[] parts = fieldName.split("\\.");
            if (parts.length > 1) {
                Object fieldValue = entry.getValue();
                if (fieldValue instanceof Collection) {
                    Collection<?> c = (Collection<?>) fieldValue;
                    for (Object o : c) {
                        addElement(e, parts[0], fieldName, o);
                    }
                } else {
                    addElement(e, parts[0], fieldName, fieldValue);
                }
            }
        }
        return e;
    }

    @Deprecated // Move this to a general purpose lib.
    public static String toUtcDateIfPossible(String dateStr) {
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // Add other parsing formats to try as you like:
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(new SimpleDateFormat(dateFormat).parse(dateStr));
            } catch (ParseException ignore) {
            }
        }
        return dateStr;
    }

    TreeSet<String> accDomainExts = new TreeSet<>();

    void addElement(final Entry e, final String prefix, final String fieldName, final Object fieldValue) {

        // TODO Should handle the collection properly
        // rather than just doing toString
        accDomainExts.add(prefix);
        if (namespaces.containsKey(prefix) || true) {
            Element element = e.addExtension(new QName(namespaces.get(prefix),
                    fieldName.substring(prefix.length() + 1), prefix));

            if (fieldValue instanceof Date) {
                element.setText(DateFormatter.format((Date) fieldValue, DateFormat.W3CDTF));
            } else if (fieldName.equalsIgnoreCase("dc.language")) {
                if (fieldValue.toString().equalsIgnoreCase("svenska")) {
                    element.setText("swe");
                } else if (fieldValue.toString().equalsIgnoreCase("engelska")) {
                    element.setText("eng");
                } else if (fieldValue.toString().equalsIgnoreCase("norska")) {
                    element.setText("nor");
                } else {
                    element.setText(fieldValue.toString());
                }
            } else {
                element.setText(fieldValue.toString());
            }
        } else {
            LOGGER.debug("Unknown namespace {}, field {} is ignored.", new Object[]{prefix, fieldName});
        }
    }

    Feed populateFeed(final Feed f, final Collection<Map<String, Object>> hits) {
        for (Map<String, Object> map : hits) {
            Entry e = f.addEntry();
            populateEntry(e, map);
        }
        return f;
    }

    UriInfo getContext() {
        return context;
    }

    void setContext(UriInfo context) {
        this.context = context;
    }

    @Override
    @GET
    @Produces({"application/xml", "application/atom+xml;type=feed;charset=UTF-8"})
    @Path("/{id}")
    public Feed getIFeedShorter(Long id, String sortField, String sortDirection) {
        return getIFeed(id, sortField, sortDirection);
    }
}
