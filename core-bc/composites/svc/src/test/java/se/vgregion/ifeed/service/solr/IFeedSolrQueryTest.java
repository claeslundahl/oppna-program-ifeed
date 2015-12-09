package se.vgregion.ifeed.service.solr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IFeedSolrQueryTest {

    IFeedSolrQuery solrQuery;

    /*@Mock
    CommonsHttpSolrServer commonsHttpSolrServer;*/

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //        solrQuery = new IFeedSolrQuery();
    }

    @Test
    public void shouldReversSortOrder() throws Exception {
        assertEquals(IFeedSolrQuery.SortDirection.desc, IFeedSolrQuery.SortDirection.asc.reverse());
        assertEquals(IFeedSolrQuery.SortDirection.asc, IFeedSolrQuery.SortDirection.desc.reverse());
    }

    @Test
    public void toTextIfDate() {
        String expected = "foo9999999999";
        assertEquals(13, expected.length());
        assertEquals(expected, IFeedSolrQuery.formatTextBeforeSorting("foo"));
        Date date = new Date();
        String result = IFeedSolrQuery.formatTextBeforeSorting(date);
        assertNotNull(result);
    }

}
