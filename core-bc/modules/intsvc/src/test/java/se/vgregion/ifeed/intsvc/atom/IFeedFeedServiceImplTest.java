package se.vgregion.ifeed.intsvc.atom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

public class IFeedFeedServiceImplTest {

    IFeedFeedServiceImpl serv;
    private IFeedSolrQuery solrQuery;
    private IFeedService iFeedService;
    UriInfo context;

    @Before
    public void setUp() throws Exception {
        iFeedService = mock(IFeedService.class);
        context = mock(UriInfo.class);
        solrQuery = mock(IFeedSolrQuery.class);
        serv = new IFeedFeedServiceImpl(solrQuery, iFeedService);

        URI uri = new URI("absolutePath");
        when(context.getAbsolutePath()).thenReturn(uri);
        serv.setContext(context);
    }

    // @Test
    // public void setPushServerEndpoint() {
    // String pushServerEndpoint;
    // serv.setPushServerEndpoint(pushServerEndpoint);
    // }
    //
    // @Test
    // public void testSetNamespaces() {
    // fail("Not yet implemented");
    // }
    //
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
    // @Test
    // public void testAddElement() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testPopulateFeed() {
    // fail("Not yet implemented");
    // }

}
