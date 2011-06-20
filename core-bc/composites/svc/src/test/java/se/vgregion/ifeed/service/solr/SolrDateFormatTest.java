/**
 * 
 */
package se.vgregion.ifeed.service.solr;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author anders
 * 
 */
public class SolrDateFormatTest {

    @Test
    public void shouldGenerateAZuluFormatedDateStringFromString() throws Exception {
        String expectedDateString = "2000-01-01T00:00:00.000Z";
        String acutalDateString = SolrDateFormat.format("2000", "01", "01");

        assertEquals(expectedDateString, acutalDateString);
        expectedDateString = "2000-02-01T00:00:00.000Z";
        acutalDateString = SolrDateFormat.format("2000", "02", "01");

        assertEquals(expectedDateString, acutalDateString);
        expectedDateString = "2000-03-01T00:00:00.000Z";
        acutalDateString = SolrDateFormat.format("2000", "03", "01");

        assertEquals(expectedDateString, acutalDateString);
    }
}
