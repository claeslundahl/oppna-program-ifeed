package se.vgregion.ifeed.service.solr;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SolrQueryBuilderTest {

    IFeedService iFeedService;

    @Before
    public void setUp() {
        iFeedService = new IFeedServiceImpl() {
            @Override
            public Map<String, FieldInf> mapFieldInfToId() {
                return new HashMap<>();
            }
        };
    }

    @Test
    @Ignore
    public void shouldHandleFreeTextSearch() throws Exception {
        String expectedQueryString = "dc.title:A\\ title";

        IFeedFilter filter = new IFeedFilter(Filter.TITLE, "A title", "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter, iFeedService.mapFieldInfToId());
        assertEquals(expectedQueryString, queryString);
    }

    @Test
    public void shouldHandleFixedTextSearch() throws Exception {
        //String expectedQueryString = "language:\"Svenska\"";
        String expectedQueryString = "language:Svenska";

        IFeedFilter filter = new IFeedFilter(Filter.LANGUAGE, "Svenska", "foo.bar.baz");
        Map<String, FieldInf> map = iFeedService.mapFieldInfToId();

        String queryString = SolrQueryBuilder.createQuery(filter, map);
        assertEquals(expectedQueryString, queryString);
    }

    @Test
    public void shouldEnforceStartDate() throws Exception {
        String fromDate = "2011-01-01T00:00:00.000Z";
        String expectedQueryString = "dc.date.validto:[" + fromDate + " TO *]";

        IFeedFilter filter = new IFeedFilter(Filter.VALID_FROM_DATE, fromDate, "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter, iFeedService.mapFieldInfToId());
        assertEquals(expectedQueryString, queryString);
    }

    @Test
    public void shouldEnforceEndDate() throws Exception {
        String toDate = "2011-01-01T00:00:00.000Z";
        String expectedQueryString = "dc.date.validfrom:[* TO " + toDate + "]";

        IFeedFilter filter = new IFeedFilter(Filter.VALID_TO_DATE, toDate, "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter, iFeedService.mapFieldInfToId());
        assertEquals(expectedQueryString, queryString);
    }

}
