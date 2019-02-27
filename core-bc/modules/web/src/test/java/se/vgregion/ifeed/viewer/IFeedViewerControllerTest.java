package se.vgregion.ifeed.viewer;

import junit.framework.Assert;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.ui.Model;
import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.regex.Pattern;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Ignore
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

    @Ignore
    @Test
    public void getIFeed() throws SolrServerException {
        Long listId = 101l;
        Model model = mock(Model.class);
        String sortField = "";
        String sortDirection = "asc";
        IFeed feed = new IFeed();
        feed.setId(listId);
        // IFeed retrievedFeed = iFeedService.getIFeedById(listId);
        when(iFeedService.getIFeed(listId)).thenReturn(feed);

        // To avoid null-pointer
        QueryResponse queryResponse = mock(QueryResponse.class);
        when(queryResponse.getResults()).thenReturn(new SolrDocumentList());

        when(solrServer.query(any(IFeedSolrQuery.class))).thenReturn(queryResponse);

        String result = controller.getIFeedById(listId, model, sortField, sortDirection, null, null, null, null /*, null*/);
        verify(iFeedService).getIFeed(listId);
        Assert.assertEquals("documentList", result);
    }

    @Test
    public void details() {
        String documentId = "1";
        Model model = mock(Model.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String result = controller.details(documentId, model, response);
        Assert.assertEquals("documentDetails", result);


        String foo = "<iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/91940/?by=dc.title&amp;dir=asc' id='iframenoscript' name='iframenoscript' style='width: 100%; height: 400px' frameborder='0'>\n" +
                "</iframe>";

        System.out.println(foo.substring(foo.indexOf("src='") + 5, foo.indexOf("/iFeed-web/")));
    }

    @Test
    public void foo() {
        System.out.println("SE2321000131-E000000000001".matches("SE[0-9]{10}\\-E[0-9]{12}"));
    }

    @Test
    public void testSomeTimeParsing() throws ParseException {
        String string = "2015-04-10T08:34:00.000Z";
        /*String defaultTimezone = TimeZone.getDefault().getID();
        Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(string.replaceAll("Z$", "+0000"));

        System.out.println("string: " + string);
        System.out.println("defaultTimezone: " + defaultTimezone);
        System.out.println("date: " + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date));


        String matching = "^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]+)?(Z|[+-](?:2[0-3]|[0-1][0-9]):[0-5][0-9])?$";
        Pattern c = Pattern.compile(matching);
        boolean result = c.matcher(string).matches();
        System.out.println(result);*/


        System.out.println(IFeedViewerController.toTextDateImpl(string));
    }

    public static void main(String[] args) {
        String stuff = "body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,textarea,p,blockquote,th,td";
        String[] parts = stuff.split(Pattern.quote(","));
        for (int i = 0; i < parts.length; i++) {
            parts[i] = "#table-container " + parts[i];
        }
        System.out.println(String.join(", ", parts));
    }

    @Test
    public void isTimeStampInFuture() {
        System.out.println(IFeedViewerController.isTimeStampInFuture("2019-02-24T00:00:00Z"));
    }

    @Test
    public void isTimeStampPassed() {
        System.out.println(IFeedViewerController.isTimeStampPassed("2019-02-28T00:00:00Z"));
    }

}