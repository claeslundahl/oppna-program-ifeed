/**
 * 
 */
package se.vgregion.ifeed.service.alfresco.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Anders Asplund
 *
 */
public class AlfrescoDocumentService {
    private String serviceLocation;
    private RestTemplate restTemplate;

    public AlfrescoDocumentService(RestTemplate restTemplate, String serviceLocation) {
        this.serviceLocation = serviceLocation;
        this.restTemplate = restTemplate;
    }

    /**
     * @param documentId
     * @return
     */
    public DocumentInfo getDocumentInfo(String documentId) {
        final ResponseEntity<DocumentInfo> responeEntity = restTemplate.getForEntity(serviceLocation, DocumentInfo.class, documentId);
        DocumentInfo documentInfo = null;
        if(responeEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            documentInfo = responeEntity.getBody();
        } else if(responeEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return null;
        }
        return documentInfo;
    }
}
