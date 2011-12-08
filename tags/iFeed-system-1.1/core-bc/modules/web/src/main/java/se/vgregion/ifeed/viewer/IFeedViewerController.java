package se.vgregion.ifeed.viewer;

import static se.vgregion.common.utils.CommonUtils.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;
import se.vgregion.ifeed.service.exceptions.IFeedServiceException;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.types.IFeed;

@Controller
public class IFeedViewerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedViewerController.class);

    private IFeedService iFeedService;
    private IFeedSolrQuery solrQuery;
    private AlfrescoDocumentService alfrescoMetadataService;

    @Autowired
    public IFeedViewerController(IFeedSolrQuery solrQuery, IFeedService iFeedService,
            AlfrescoDocumentService documentService) {
        this.solrQuery = solrQuery;
        this.iFeedService = iFeedService;
        this.alfrescoMetadataService = documentService;
    }

    @RequestMapping(value = "/documentlists/{listId}")
    public String getIFeed(@PathVariable Long listId, Model model,
            @RequestParam(value = "by", required = false) String sortField,
            @RequestParam(value = "dir", required = false) String sortDirection) {

        // Retrieve feed from store
        IFeed retrievedFeed = iFeedService.getIFeed(listId);

        if (retrievedFeed == null) {
            // Throw 404 if the feed doesn't exist
            throw new ResourceNotFoundException();
        }
        List<Map<String, Object>> result = solrQuery.getIFeedResults(retrievedFeed, sortField,
                getEnum(SortDirection.class, sortDirection));

        model.addAttribute("result", result);
        return "documentList";
    }

    @RequestMapping(value = "/documents/{documentId}")
    public String details(@PathVariable String documentId, Model model) {
        String fullId = "workspace://SpacesStore/" + documentId;
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(fullId);
        model.addAttribute("documentInfo", documentInfo);

        return "documentDetails";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
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
