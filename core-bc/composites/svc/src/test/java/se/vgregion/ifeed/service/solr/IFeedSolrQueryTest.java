package se.vgregion.ifeed.service.solr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.text.Collator;
import java.util.*;

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

    @Test
    public void getSwedishComparator() {
        Collator collator = IFeedSolrQuery.getSwedishComparator();
        assertEquals(1, collator.compare("ä", "å"));
    }

    @Test
    public void sort() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> m1 = new HashMap<String, Object>();
        m1.put("f", "Ä");

        Map<String, Object> m2 = new HashMap<String, Object>();
        m2.put("f", "Å");

        Map<String, Object> m3 = new HashMap<String, Object>();
        m3.put("f", "Ö");

        Map<String, Object> m4 = new HashMap<String, Object>();
        m4.put("f", null);


        Map<String, Object> m5 = new HashMap<String, Object>();
        m5.put("f", "Ö");

        list.add(m1);
        list.add(m2);
        list.add(m3);
        list.add(m4);
        list.add(m5);

        IFeedSolrQuery.sort(list, "f", 1);

        for (Map<String, Object> m : list) System.out.println(m);

        list.remove(m4);
        assertEquals(m1, list.get(1));
        assertEquals(m2, list.get(0));
    }


}
