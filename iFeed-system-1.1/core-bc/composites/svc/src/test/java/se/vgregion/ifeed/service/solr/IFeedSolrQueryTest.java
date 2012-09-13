package se.vgregion.ifeed.service.solr;

import static org.junit.Assert.*;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IFeedSolrQueryTest {

    IFeedSolrQuery solrQuery;

    @Mock
    CommonsHttpSolrServer commonsHttpSolrServer;

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

}
