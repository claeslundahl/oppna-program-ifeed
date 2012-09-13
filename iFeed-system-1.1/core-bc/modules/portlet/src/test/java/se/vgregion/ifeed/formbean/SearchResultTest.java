package se.vgregion.ifeed.formbean;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import se.vgregion.ifeed.formbean.SearchResultList.SearchResult;
import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.DateFormatter.DateFormat;

public class SearchResultTest {

    SearchResult result;
    private Date processingTime;
    private String title;
    private String link;
    private String documentId;

    public void setUpDataForOtherDay() {
        processingTime = new Date(0l);
        setUp();
    }

    public void setUpDataForThisDay() {
        processingTime = new Date();
        setUp();
    }

    public void setUp() {
        title = "title";
        link = "link";
        documentId = "documentId";

        result = new SearchResult(processingTime, title, link, documentId);
    }

    @Test
    public void getProcessingTime_not_this_day() {
        setUpDataForOtherDay();
        String expected = DateFormatter.format(processingTime, DateFormat.DATE_ONLY);
        assertEquals(expected, result.getProcessingTime());
    }

    @Test
    public void getProcessingTime_on_this_day() {
        setUpDataForThisDay();
        String expected = DateFormatter.format(processingTime, DateFormat.TIME_ONLY);
        assertEquals(expected, result.getProcessingTime());
    }

}
