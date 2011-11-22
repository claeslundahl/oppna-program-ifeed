package se.vgregion.ifeed.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.ActionResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import se.vgregion.ifeed.service.ifeed.IFeedService;
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
        controller.setiFeedService(iFeedService);
    }

    @Test
    public void editIFeedLongModelActionResponse() {
        Long feedId = 101l;
        IFeed feed = new IFeed();
        when(iFeedService.getIFeed(feedId)).thenReturn(feed);

        controller.editIFeed(feedId, model, response);
        verify(model).addAttribute(eq("ifeed"), any(IFeed.class));
    }

    /*@Test public void testShowEditIFeedForm() { fail("Not yet implemented"); }
     * 
     * @Test public void testEditIFeedIFeedActionResponseModelPortletRequest() { fail("Not yet implemented"); }
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
