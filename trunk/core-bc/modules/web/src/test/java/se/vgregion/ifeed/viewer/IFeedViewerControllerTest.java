package se.vgregion.ifeed.viewer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
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
    private IFeedService iFeedService;
    private AlfrescoDocumentService documentService;
    private SolrServer solrServer;

    @Before
    public void setUp() throws Exception {
        iFeedService = mock(IFeedService.class);
        documentService = mock(AlfrescoDocumentService.class);
        solrServer = mock(SolrServer.class);

        controller = new IFeedViewerController(iFeedService, documentService, solrServer);
    }

    @Test
    public void getIFeed() throws SolrServerException {
        Long listId = 101l;
        Model model = mock(Model.class);
        String sortField = "";
        String sortDirection = "asc";
        IFeed feed = new IFeed();
        feed.setId(listId);
        // IFeed retrievedFeed = iFeedService.getIFeed(listId);
        when(iFeedService.getIFeed(listId)).thenReturn(feed);

        // To avoid null-pointer
        QueryResponse queryResponse = mock(QueryResponse.class);
        when(queryResponse.getResults()).thenReturn(new SolrDocumentList());

        when(solrServer.query(any(IFeedSolrQuery.class))).thenReturn(queryResponse);

        String result = controller.getIFeed(listId, model, sortField, sortDirection, null, null);
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
