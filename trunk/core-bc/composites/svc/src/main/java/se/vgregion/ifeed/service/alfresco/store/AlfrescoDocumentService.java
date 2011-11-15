/**
 * 
 */
package se.vgregion.ifeed.service.alfresco.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import se.vgregion.ifeed.service.exceptions.IFeedServiceException;

/**
 * @author Anders Asplund
 *
 */
public class AlfrescoDocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(AlfrescoDocumentService.class);

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
        DocumentInfo documentInfo = null;
        try {
            final ResponseEntity<DocumentInfo> responeEntity = restTemplate.getForEntity(serviceLocation, DocumentInfo.class, documentId);
            if(responeEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                documentInfo = responeEntity.getBody();
            } else {
                final String msg = "No metadata found for document id: " + documentId;
                LOG.warn(msg);
                throw new IFeedServiceException("error.documentid.unknown", msg);
            }
        } catch (RestClientException e) {
            final String msg = "Unable to connect to Alfresco document service";
            LOG.error(msg);
            throw new IFeedServiceException("error.alfresco.connection", msg, e);
        }
        return documentInfo;
    }
}
