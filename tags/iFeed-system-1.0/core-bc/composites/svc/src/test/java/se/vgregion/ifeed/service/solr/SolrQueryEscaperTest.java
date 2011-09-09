package se.vgregion.ifeed.service.solr;

import static org.junit.Assert.*;

import org.junit.Test;


public class SolrQueryEscaperTest {

    @Test
    public void shouldEscapeSpecialCharacters() throws Exception {
        String strToEscape = "(1+1):2";
        String excpectedStr = "\\(1\\+1\\)\\:2";
        String actualStr = SolrQueryEscaper.escape(strToEscape);
        assertEquals(excpectedStr, actualStr);

        strToEscape = "a&&b";
        excpectedStr = "a\\&&b";
        actualStr = SolrQueryEscaper.escape(strToEscape);
        assertEquals(excpectedStr, actualStr);

    }
}
