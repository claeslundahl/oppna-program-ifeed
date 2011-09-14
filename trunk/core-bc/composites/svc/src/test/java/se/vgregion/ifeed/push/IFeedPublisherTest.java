package se.vgregion.ifeed.push;


import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.ifeed.service.push.IFeedPublisher;
import se.vgregion.ifeed.types.IFeed;

public class IFeedPublisherTest {

    private IFeedPublisher publisher = null;

    @Before
    public void doSetup() throws Exception {
        publisher = new IFeedPublisher(new URL("http://example.com"));
        publisher.setIfeedAtomFeed(
            "http://localhost:8080/iFeed-core-bc-module-intsvc/ifeeds/%s/feed");
    }

    @Test
    public final void shouldGenerateAPostBodyWithIfeedLinks() throws Exception {
        int ifeeds = 3;
        IFeed ifeed;
        for (int i = 0; i < ifeeds; i++) {
            ifeed = new IFeed();
            ifeed.setId((long) i);
            publisher.addIFeed(ifeed);
        }

        assertEquals(ifeeds, publisher.getIFeedCount());
        assertEquals(ifeeds, StringUtils.countMatches(
                publisher.getRequestBody(), "hub.url"));

    }

}
