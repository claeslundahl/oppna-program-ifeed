package se.vgregion.ifeed.intsvc.atom;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.types.IFeed;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class IFeedFeedServiceImplTest {

    IFeedFeedServiceImpl serv;

    private IFeedService iFeedService;

    @Before
    public void setUp() throws Exception {

        iFeedService = new IFeedServiceImpl(null, null, null) {
            @Override
            public IFeed getIFeed(Long id) {
                IFeed feed = new IFeed();
                feed.setName("feedName");
                return feed;
            }

        };

        serv = new IFeedFeedServiceImpl(null, iFeedService);

        URI uri = new URI("absolutePath");
        // when(context.getAbsolutePath()).thenReturn(uri);
        // serv.setContext(context);
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

}
