package se.vgregion.ifeed.service.solr;

import static org.junit.Assert.assertEquals;
import static
    se.vgregion.ifeed.service.solr.DateFormatter.DateFormats.SOLR_DATE_FORMAT;
import static se.vgregion.ifeed.service.solr.DateFormatter.DateFormats.W3CDTF;

import org.junit.Test;

/**
 * @author anders
 *
 */
public class SolrDateFormatTest {

    @Test
    public void shouldGenerateDateStringFormattedAsSolrDate() throws Exception {
        String expectedDateString = "2000-01-01T00:00:00.000Z";
        String acutalDateString = DateFormatter.format(
                2000, 01, 01, SOLR_DATE_FORMAT);

        assertEquals(expectedDateString, acutalDateString);
        expectedDateString = "2000-02-01T00:00:00.000Z";
        acutalDateString = DateFormatter.format(
                2000, 02, 01, SOLR_DATE_FORMAT);

        assertEquals(expectedDateString, acutalDateString);
        expectedDateString = "2000-03-01T00:00:00.000Z";
        acutalDateString = DateFormatter.format(
                2000, 03, 01, SOLR_DATE_FORMAT);

        assertEquals(expectedDateString, acutalDateString);
    }

    @Test
    public void shouldGenerateDateStringFormattedAsW3CDTF() throws Exception {
        String expectedDateString = "2000-01-01T00:00:00Z";
        String acutalDateString = DateFormatter.format(2000, 01, 01, W3CDTF);

        assertEquals(expectedDateString, acutalDateString);
        expectedDateString = "2000-02-01T00:00:00Z";
        acutalDateString = DateFormatter.format(2000, 02, 01, W3CDTF);

        assertEquals(expectedDateString, acutalDateString);
        expectedDateString = "2000-03-01T00:00:00Z";
        acutalDateString = DateFormatter.format(2000, 03, 01, W3CDTF);

        assertEquals(expectedDateString, acutalDateString);
    }
}
