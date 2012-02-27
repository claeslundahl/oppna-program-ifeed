package se.vgregion.ifeed.service.solr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

public class SolrQueryBuilderTest {

    @Test
    public void shouldHandleFreeTextSearch() throws Exception {
        String expectedQueryString = "dc.title:\"A title\"";

        IFeedFilter filter = new IFeedFilter(Filter.TITLE, "A title", "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter);
        assertEquals(expectedQueryString, queryString);
    }

    @Test
    public void shouldHandleFixedTextSearch() throws Exception {
        String expectedQueryString = "dc.language:\"Svenska\"";

        IFeedFilter filter = new IFeedFilter(Filter.LANGUAGE, "Svenska", "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter);
        assertEquals(expectedQueryString, queryString);
    }

    @Test
    public void shouldEnforceStartDate() throws Exception {
        String fromDate = "2011-01-01T00:00:00.000Z";
        String expectedQueryString = "dc.date.validto:[" + fromDate + " TO *]";

        IFeedFilter filter = new IFeedFilter(Filter.VALID_FROM_DATE, fromDate, "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter);
        assertEquals(expectedQueryString, queryString);
    }

    @Test
    public void shouldEnforceEndDate() throws Exception {
        String toDate = "2011-01-01T00:00:00.000Z";
        String expectedQueryString = "dc.date.validfrom:[* TO " + toDate + "]";

        IFeedFilter filter = new IFeedFilter(Filter.VALID_TO_DATE, toDate, "foo.bar.baz");
        String queryString = SolrQueryBuilder.createQuery(filter);
        assertEquals(expectedQueryString, queryString);
    }
}
