package se.vgregion.ifeed.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.web.util.UriTemplate;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

public class EditIFeedControllerTest {

    EditIFeedController controller;
    private Model model;
    private ActionResponse response;
    IFeedService iFeedService;

    @Before
    public void setUp() throws Exception {
        controller = new EditIFeedController();
        model = mock(Model.class);
        response = mock(ActionResponse.class);
        iFeedService = mock(IFeedService.class);
        controller.setIFeedService(iFeedService);
    }

    @Test
    public void editIFeedLongModelActionResponse() {
        Long feedId = 101l;
        IFeed feed = new IFeed();
        when(iFeedService.getIFeed(feedId)).thenReturn(feed);

        controller.editIFeed(feedId, model, response);
        verify(model).addAttribute(eq("ifeed"), any(IFeed.class));
    }

    @Test
    public void showEditIFeedForm() {
        IFeed iFeed = new IFeed();
        iFeed.setId(200l);
        iFeed.setSortField("id");
        iFeed.setSortDirection("asc");

        String orderByCol = "id";
        String orderByType = "asc";
        javax.portlet.RenderResponse response = mock(RenderResponse.class);

        IFeedSolrQuery iFeedSolrQuery = mock(IFeedSolrQuery.class);
        when(iFeedSolrQuery.getIFeedResults(iFeed)).thenReturn(new ArrayList<Map<String, Object>>());
        when(iFeedSolrQuery.getRows()).thenReturn(10);
        controller.setIFeedSolrQuery(iFeedSolrQuery);

        UriTemplate iFeedAtomFeed = mock(UriTemplate.class);
        controller.setIFeedAtomFeed(iFeedAtomFeed);

        PortletURL purl = mock(PortletURL.class);
        when(response.createRenderURL()).thenReturn(purl);

        controller.showEditIFeedForm(iFeed, orderByCol, orderByType, model, response);
    }
    /* @Test public void testEditIFeedIFeedActionResponseModelPortletRequest() { fail("Not yet implemented"); }
     * 
     * @Test public void testUpdateIFeed() { fail("Not yet implemented"); }
     * 
     * @Test public void testSelectNewFilter() { fail("Not yet implemented"); }
     * 
     * @Test public void testAddFilter() { fail("Not yet implemented"); }
     * 
     * @Test public void testEditFilter() { fail("Not yet implemented"); }
     * 
     * @Test public void testRemoveFilter() { fail("Not yet implemented"); }
     * 
     * @Test public void testCancel() { fail("Not yet implemented"); }
     * 
     * @Test public void testSearchPeople() { fail("Not yet implemented"); }
     * 
     * @Test public void testGetFilters() { fail("Not yet implemented"); }
     * 
     * @Test public void testGetFilterTypes() { fail("Not yet implemented"); } */
}
