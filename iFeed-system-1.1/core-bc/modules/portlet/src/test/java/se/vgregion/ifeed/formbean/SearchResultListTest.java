package se.vgregion.ifeed.formbean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.ifeed.formbean.SearchResultList.SearchResult;

public class SearchResultListTest {

    SearchResultList searchResult;
    List<Map<String, Object>> iFeedResults;
    Map<String, Object> firstItem;

    @Before
    public void setUp() {
        firstItem = new HashMap<String, Object>();
        iFeedResults = new ArrayList<Map<String, Object>>();
        iFeedResults.add(firstItem);
        searchResult = new SearchResultList(iFeedResults);

        firstItem.put("dc.identifier.documentid", "documentId");
        firstItem.put("processingtime", new Date(0l));
        firstItem.put("title", "titleValue");
        firstItem.put("url", "urlValue");
    }

    @Test
    public void size() {
        assertEquals(1, searchResult.size());
    }

    @Test
    public void get() {
        SearchResult result = searchResult.get(0);
        checkCorrespondingValuesEquals(firstItem, result);
    }

    public void checkCorrespondingValuesEquals(Map<String, Object> map, SearchResult result) {
        assertEquals(map.get("dc.identifier.documentid"), result.getDocumentId());
        // assertEquals(firstItem.get("processingtime"), result.getProcessingTime());
        // Test the getProcessingTime() explicitly in test for this class...
        assertEquals(map.get("title"), result.getTitle());
        assertEquals(map.get("url"), result.getLink());
    }

}
