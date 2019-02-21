package se.vgregion.ifeed.intsvc.atom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.client.ScriptEngineFactory;
import se.vgregion.ifeed.types.IFeed;

public class IFeedFeedServiceImplTest {

    IFeedFeedServiceImpl serv;
    private IFeedSolrQuery solrQuery;
    private IFeedService iFeedService;
    UriInfo context;

    @Before
    public void setUp() throws Exception {
        ScriptEngineFactory.setScriptEngineFactory(new ScriptEngineFactory() {
            @Override
            public ScriptEngine createJavascriptScriptEngine() {
                ScriptEngineManager sem = new ScriptEngineManager();
                return sem.getEngineByName("javascript");
            }
        });

        iFeedService = mock(IFeedService.class);
        context = mock(UriInfo.class);
        solrQuery = mock(IFeedSolrQuery.class);
        serv = new IFeedFeedServiceImpl(solrQuery, iFeedService);

        URI uri = new URI("absolutePath");
        when(context.getAbsolutePath()).thenReturn(uri);
        serv.setContext(context);
    }

    /*
    @Test
    public void getIFeed() throws URISyntaxException {
        Long id = 100l;
        String sortField = "sortField";
        String sortDirection = "asc";

        IFeed feed = mock(IFeed.class);
        when(feed.getName()).thenReturn("feedName");
        when(iFeedService.getIFeed(id)).thenReturn(feed);

        Feed result = serv.getIFeed(id, sortField, sortDirection);
        assertEquals(feed.getName(), result.getTitle());
    }
    */

    @Test
    public void getIFeedEntry_error_on_no_result() {
        try {
            Entry result = serv.getIFeedEntry(100l);
            fail();
        } catch (WebApplicationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void getIFeedEntry() {
        long id = 100l;
        IFeed iFeed = new IFeed();
        when(iFeedService.getIFeed(id)).thenReturn(iFeed);
        iFeed.setName("feedName");

        Entry result = serv.getIFeedEntry(id);
        assertEquals(iFeed.getName(), result.getTitle());
    }

    @Test
    public void populateEntry_checked_default_values() {
        Entry entry = mock(Entry.class);
        Map<String, Object> map = new HashMap<String, Object>();
        Entry result = serv.populateEntry(entry, map);

        verify(entry).setTitle(any(String.class));
        verify(entry).addAuthor(any(String.class));
        verify(entry).setUpdated(any(Date.class));
    }

    @Ignore
    @Test
    public void populateEntry_non_default_values() {
        Entry entry = mock(Entry.class);
        Map<String, Object> map = new HashMap<String, Object>();

        String title = "t", author = "a", summary = "s";
        Date updated = new Date(1000l);

        map.put("dc.creator", author);
        map.put("processingtime", updated);
        map.put("dc.title", title);
        map.put("dc.description", summary);

        Entry result = serv.populateEntry(entry, map);

        map.put("url", "http://acme.com");
        map.put("dc.format", "html");

        List<Integer> ints = Arrays.asList(1, 2, 3);
        map.put("ints.0", ints);

        verify(entry).setTitle(eq(title));
        verify(entry).addAuthor(eq(author));
        verify(entry).setUpdated(eq(updated));
        verify(entry).setSummary(eq(summary));

        assertEquals(result, entry);
    }

    @Test
    public void addElementDate() {
        String prefix = "prefix";
        String fieldName = "fieldName";
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("prefix", "prefix0");
        addElement(prefix, fieldName, ns, new Date(0l));
    }

    @Test
    public void addElementLanguages() {
        String fieldName = "dc.language";
        // Map<String, String> ns = new HashMap<String, String>();
        // ns.put("prefix", "prefix0");

        Map<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("svenska", "swe");
        prefixes.put("norska", "nor");
        prefixes.put("engelska", "eng");
        serv.setNamespaces(prefixes);

        for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
            Element element = addElement(prefix.getKey(), fieldName, prefixes, prefix.getKey());
            verify(element).setText(eq(prefix.getValue()));
        }
    }

    public Element addElement(String prefix, String fieldName, Map<String, String> ns, Object value) {
        Entry e = mock(Entry.class);
        Element element = mock(Element.class);

        when(e.addExtension(new QName(ns.get(prefix), fieldName.substring(prefix.length() + 1), prefix)))
                .thenReturn(element);

        serv.setNamespaces(ns);
        serv.addElement(e, prefix, fieldName, value);

        return element;
    }

    @Test
    public void populateFeed() {
        Feed f = mock(Feed.class);
        Entry entry = mock(Entry.class);
        when(f.addEntry()).thenReturn(entry);
        Collection<Map<String, Object>> hits = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("key", "value");
        hits.add(item);

        serv.populateFeed(f, hits);
    }

}
