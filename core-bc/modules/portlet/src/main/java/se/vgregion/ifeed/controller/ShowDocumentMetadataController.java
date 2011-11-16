package se.vgregion.ifeed.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;
import se.vgregion.ifeed.service.exceptions.IFeedServiceException;

@Controller
@RequestMapping("VIEW")
public class ShowDocumentMetadataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowDocumentMetadataController.class);

    private AlfrescoDocumentService alfrescoMetadataService;

    private List<String> topListMetadata;

    @Autowired
    private MessageSource resource;

    public ShowDocumentMetadataController(AlfrescoDocumentService documentService) {
        this.alfrescoMetadataService = documentService;
    }

    public void setTopListMetadata(List<String> topListMetadata) {
        this.topListMetadata = topListMetadata;
    }

    @RenderMapping(params = "view=documentMetadata")
    public String showDocumentMetadata(Model model, @RequestParam(required = true) final String documentId) {
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(documentId);
        model.addAttribute("documentInfo", documentInfo);

        return "documentMetadata";
    }

    @ResourceMapping
    public void showTopDocumentMetadata(@RequestParam(required = true) final String documentId,
            ResourceResponse response) {
        Map<String, String> metadataPair = getMetdataValues(documentId);
        sendJsonResponse(response, metadataPair);
    }

    private Map<String, String> getMetdataValues(final String documentId) {
        Map<String, String> metadataPair = new HashMap<String, String>(topListMetadata.size());
        DocumentInfo documentInfo = null;

        try {
            documentInfo = alfrescoMetadataService.getDocumentInfo(documentId);
            LOGGER.debug("Document info retrieved from alfresco: {}", documentInfo);

            String metaValue = "";
            for (String metadata : topListMetadata) {
                Object metaValueObj = documentInfo.getMetadataValue(metadata, Object.class);
                metaValue = metaValueObj.toString();
                if(StringUtils.isBlank(metaValue)) {
                    LOGGER.warn("Metadata, {}, not found in document with id: {}", metadata, documentId);
                }
                metadataPair.put(resource.getMessage(metadata + ".label", null, metadata, null), metaValue);
            }
        } catch (IFeedServiceException e) {
            metadataPair.put("Varning", "Ingen metadata kunde hittas!");
        }
        return metadataPair;
    }

    private void sendJsonResponse(ResourceResponse response, Map<String, String> metadataPair) {
        try{
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            new ObjectMapper().writeValue(out, metadataPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAlfrescoDocumentServiceException(Exception e) {
        if (!(e instanceof IFeedServiceException)) {
            LOGGER.error("IFeed Excption: {}", e);
            e = new IFeedServiceException("error.unhandled", "Internal IFeed Error", e);
        }
        return new ModelAndView("ExceptionHandler", Collections.singletonMap("exception", e));
    }
}