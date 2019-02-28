package se.vgregion.ifeed.controller;

import org.junit.Ignore;

@Ignore
public class ShowDocumentMetadataControllerTest {

    /*ShowDocumentMetadataController sdmc;
    ResourceResponse resourceResponse;
    AlfrescoDocumentService ads;*/

    /*@Before
    public void setUp() throws Exception {
        ads = mock(AlfrescoDocumentService.class);
        sdmc = new ShowDocumentMetadataController(ads);
        resourceResponse = mock(ResourceResponse.class);
    }

    @Test
    public void showTopDocumentMetadata() throws IOException {
        sdmc.setTopListMetadata(new ArrayList<String>());
        OutputStream os = mock(OutputStream.class);
        when(resourceResponse.getPortletOutputStream()).thenReturn(os);

        final Map<String, String> getMetdataValuesResult = new HashMap<String, String>();
        final ShowDocumentMetadataController sdmc;

        sdmc = new ShowDocumentMetadataController(ads) {
            @Override
            protected Map<String, String> getMetdataValues(String documentId) {
                assertEquals("abc", documentId);
                getMetdataValuesResult.put("key", "value");
                return getMetdataValuesResult;
            }

            @Override
            protected void sendJsonResponse(ResourceResponse response, Map<String, String> metadataPair) {
                assertEquals(getMetdataValuesResult, metadataPair);
                assertEquals(resourceResponse, response);
            }
        };

        sdmc.showTopDocumentMetadata("abc", resourceResponse);
    }

    @Test
    public void getMetdataValuesError() {
        sdmc.setTopListMetadata(Arrays.asList("key"));
        DocumentInfo documentInfo = mock(DocumentInfo.class);
        when(documentInfo.getMetadataValue("key", Object.class)).thenReturn("value");
        when(ads.getDocumentInfo("docId")).thenThrow(new IFeedServiceException("Title", "Explaination"));

        Map<String, String> result = sdmc.getMetdataValues("docId");
        assertTrue(result.containsKey("Varning"));
        assertTrue(result.containsValue("Ingen metadata kunde hittas!"));

    }

    @Ignore
    @Test
    public void getMetdataValues() {
        sdmc.setTopListMetadata(Arrays.asList("key"));
        DocumentInfo documentInfo = mock(DocumentInfo.class);
        when(documentInfo.getMetadataValue("key", Object.class)).thenReturn("value");
        when(ads.getDocumentInfo("docId")).thenReturn(documentInfo);

        MessageSource resource = mock(MessageSource.class);
        when(resource.getMessage("key.label", null, "key", null)).thenReturn("key1label");
        //sdmc.setResource(resource);

        Map<String, String> map = sdmc.getMetdataValues("docId");
        assertEquals("key1label", map.keySet().iterator().next());
        assertEquals("value", map.values().iterator().next());
    }

    @Test
    public void showDocumentMetadata() {
        Model model = mock(Model.class);
        IFeed iFeed = mock(IFeed.class);
        String result = sdmc.showDocumentMetadata(iFeed, model, "docId");
        assertEquals("documentMetadata", result);
    }

    @Test
    public void handleAlfrescoDocumentServiceException() {
        Exception e = new IFeedServiceException("key", "defaultMessage");
        handleAlfrescoDocumentServiceException(e, e);
        e = new RuntimeException(e);
        handleAlfrescoDocumentServiceException(e, new IFeedServiceException("error.unhandled",
                "Internal IFeed Error", e));
    }

    public void handleAlfrescoDocumentServiceException(Exception e, Exception expexted) {
        ModelAndView result = sdmc.handleAlfrescoDocumentServiceException(e);
        assertEquals(expexted.getMessage(), ((Exception) result.getModel().get("exception")).getMessage());
        assertEquals("ExceptionHandler", result.getViewName());
    }*/

}
