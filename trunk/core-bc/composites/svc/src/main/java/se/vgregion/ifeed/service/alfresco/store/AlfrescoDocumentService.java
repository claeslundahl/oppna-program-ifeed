package se.vgregion.ifeed.service.alfresco.store;

import org.springframework.web.client.RestTemplate;

/**
 * @author Anders Asplund
 *
 */
public class AlfrescoDocumentService {
    private String serviceLocation;
    private RestTemplate restTemplate;

    public AlfrescoDocumentService(final RestTemplate restTemplate,
            final String serviceLocation) {
        this.serviceLocation = serviceLocation;
        this.restTemplate = restTemplate;
    }

    public DocumentInfo getDocumentInfo(String documentId) {
        return restTemplate.getForObject(serviceLocation,
                DocumentInfo.class, documentId);
    }
}
