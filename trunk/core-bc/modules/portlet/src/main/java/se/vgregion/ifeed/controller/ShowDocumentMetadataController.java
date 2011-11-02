package se.vgregion.ifeed.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;

@Controller
@RequestMapping("VIEW")
public class ShowDocumentMetadataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ShowDocumentMetadataController.class);

    private AlfrescoDocumentService alfrescoMetadataService;

    private List<String> topListMetadata;

    @Autowired
    private MessageSource resource;

    public ShowDocumentMetadataController(AlfrescoDocumentService documentService, List<String> topListMetadata) {
        this.alfrescoMetadataService = documentService;
        this.topListMetadata = topListMetadata;
    }


    @RenderMapping(params="view=documentMetadata")
    public String showDocumentMetadata(Model model, @RequestParam(required=true) final String documentId) {
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(documentId);
        model.addAttribute("documentInfo", documentInfo);

        return "documentMetadata";
    }

    @ResourceMapping
    public void showTopDocumentMetadata(@RequestParam(required=true) final String documentId, ResourceResponse response) {
        Map<String, String> metadataPair = new HashMap<String, String>(topListMetadata.size());
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(documentId);
        String metaValue = "";
        for (String metadata : topListMetadata) {
            try {
                metaValue = documentInfo.getMetadataValue(metadata, String.class);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
                Object metaValueObj = documentInfo.getMetadataValue(metadata, Object.class);
                metaValue = metaValueObj.toString();
            }
            metadataPair.put(resource.getMessage(metadata + ".label", null, metadata, null), metaValue);
        }
        try {
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            new ObjectMapper().writeValue(out, metadataPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
