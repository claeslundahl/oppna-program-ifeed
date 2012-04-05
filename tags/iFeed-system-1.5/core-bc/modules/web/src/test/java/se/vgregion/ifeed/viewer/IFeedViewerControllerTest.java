package se.vgregion.ifeed.viewer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.exceptions.IFeedServiceException;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

public class IFeedViewerControllerTest {

    private IFeedViewerController controller;
    private IFeedSolrQuery solrQuery;
    private IFeedService iFeedService;
    private AlfrescoDocumentService documentService;

    @Before
    public void setUp() throws Exception {
        solrQuery = mock(IFeedSolrQuery.class);
        iFeedService = mock(IFeedService.class);
        documentService = mock(AlfrescoDocumentService.class);

        controller = new IFeedViewerController(solrQuery, iFeedService, documentService);
    }

    @Test
    public void getIFeed() {
        Long listId = 101l;
        Model model = mock(Model.class);
        String sortField = "";
        String sortDirection = "asc";
        IFeed feed = new IFeed();
        feed.setId(listId);
        // IFeed retrievedFeed = iFeedService.getIFeed(listId);
        when(iFeedService.getIFeed(listId)).thenReturn(feed);

        String result = controller.getIFeed(listId, model, sortField, sortDirection);
        verify(iFeedService).getIFeed(listId);
        Assert.assertEquals("documentList", result);
    }

    @Test
    public void details() {
        String documentId = "1";
        Model model = mock(Model.class);

        String result = controller.details(documentId, model);
        Assert.assertEquals("documentDetails", result);
    }

    /*@Test
    public void handleAlfrescoDocumentServiceException() {
        Exception e = new IFeedServiceException("", "");

        ModelAndView result = controller.handleAlfrescoDocumentServiceException(e);
        IFeedServiceException se = (IFeedServiceException) result.getModel().get("exception");
        Assert.assertEquals(se, e);
    }*/

}
