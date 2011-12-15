package se.vgregion.ifeed.service.alfresco.store;


public class AlfrescoDocumentServiceTest {
    //
    // @Before
    // public void setUp() throws Exception {
    // }
    //
    // @Test
    // public void getDocumentInfo_httpFailed() {
    // try {
    // getDocumentInfo(HttpStatus.BAD_REQUEST, false);
    // fail();
    // } catch (Exception e) {
    // assertTrue(true);
    // }
    // }
    //
    // @Test
    // public void getDocumentInfo_httpOk() {
    // getDocumentInfo(HttpStatus.OK, false);
    // assertTrue(true);
    // }
    //
    // @Test
    // public void getDocumentInfo_restFailed() {
    // try {
    // getDocumentInfo(HttpStatus.OK, true);
    // fail();
    // } catch (IFeedServiceException e) {
    // assertTrue(true);
    // } catch (Exception miscException) {
    // fail();
    // }
    //
    // }
    //
    // public void getDocumentInfo(HttpStatus statusCode, boolean throwRestClientException) {
    // RestTemplate restTemplate = mock(RestTemplate.class);
    // final ResponseEntity<DocumentInfo> responeEntity = mock(ResponseEntity.class);
    // String serviceLocation = "someurl";
    // String documentId = "someId";
    // DocumentInfo documentInfo = mock(DocumentInfo.class);
    // when(responeEntity.getBody()).thenReturn(documentInfo);
    // // HttpStatus statusCode = HttpStatus.OK;
    // when(responeEntity.getStatusCode()).thenReturn(statusCode);
    // if (throwRestClientException) {
    // RestClientException rce = new RestClientException("");
    // when(restTemplate.getForEntity(serviceLocation, DocumentInfo.class, documentId)).thenThrow(rce);
    // } else {
    // when(restTemplate.getForEntity(serviceLocation, DocumentInfo.class, documentId)).thenReturn(
    // responeEntity);
    // }
    //
    // AlfrescoDocumentService ads = new AlfrescoDocumentService(restTemplate, serviceLocation);
    // ads.getDocumentInfo(documentId);
    // }

}
