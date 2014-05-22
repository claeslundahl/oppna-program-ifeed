package se.vgregion.ifeed.viewer;

import static se.vgregion.common.utils.CommonUtils.getEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.common.utils.CommonUtils;
import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;
import se.vgregion.ifeed.service.exceptions.IFeedServiceException;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;

@Controller
public class IFeedViewerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedViewerController.class);

    private IFeedService iFeedService;
    private SolrServer solrServer;
    private AlfrescoDocumentService alfrescoMetadataService;

    @Autowired
    public IFeedViewerController(IFeedService iFeedService, AlfrescoDocumentService documentService,
                                 SolrServer solrServer) {
        this.solrServer = solrServer;
        this.iFeedService = iFeedService;
        this.alfrescoMetadataService = documentService;
    }

    @RequestMapping(value = "/documentlists/{listId}")
    public String getIFeedHtml(@PathVariable Long listId, Model model,
                               @RequestParam(value = "by", required = false) String sortField,
                               @RequestParam(value = "dir", required = false) String sortDirection) {
        return getIFeed(listId, model, sortField, sortDirection, null, null, null);
    }

    public static final Map<String, Integer> callsToJsonpMetadata = new TreeMap<String, Integer>();

    @RequestMapping(value = "/metadata-calls")
    public String viewCallsToJsonpMetadata(Model model) {
        model.addAttribute("keys", callsToJsonpMetadata.keySet());
        model.addAttribute("values", callsToJsonpMetadata);
        return "metadata-calls";
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @RequestMapping(value = "/documentlists/{listId}/metadata")
    public String getIFeed(@PathVariable String listIdOrSerializedInstance, Model model,
                           @RequestParam(value = "by", required = false) String sortField,
                           @RequestParam(value = "dir", required = false) String sortDirection,
                           @RequestParam(value = "startBy", required = false) Integer startBy,
                           @RequestParam(value = "endBy", required = false) Integer endBy,
                           @RequestParam(value = "fromPage", required = false) String fromPage) {
        if (isNumeric(listIdOrSerializedInstance)) {
            Long id = Long.parseLong(listIdOrSerializedInstance);
            return getIFeed(id, model, sortField, sortDirection, startBy, endBy, fromPage);
        } else {
            IFeed ifeed = (IFeed) CommonUtils.toObject(listIdOrSerializedInstance);
            return getIFeed(ifeed, model, sortField, sortDirection, startBy, endBy, fromPage);
        }
    }

    public String getIFeed(@PathVariable Long listId, Model model,
                           @RequestParam(value = "by", required = false) String sortField,
                           @RequestParam(value = "dir", required = false) String sortDirection,
                           @RequestParam(value = "startBy", required = false) Integer startBy,
                           @RequestParam(value = "endBy", required = false) Integer endBy,
                           @RequestParam(value = "fromPage", required = false) String fromPage) {
        IFeed retrievedFeed = iFeedService.getIFeed(listId);
        return getIFeed(retrievedFeed, model, sortField, sortDirection, startBy, endBy, fromPage);
    }

    public String getIFeed(IFeed retrievedFeed, Model model,
                            String sortField,
                            String sortDirection,
                            Integer startBy,
                            Integer endBy,
                            String fromPage) {

        if (fromPage != null && !"".equals(fromPage.trim())) {
            System.out.println("From Page " + fromPage);
            Integer i = callsToJsonpMetadata.get(fromPage);
            if (i == null) {
                callsToJsonpMetadata.put(fromPage, 1);
            } else {
                callsToJsonpMetadata.put(fromPage, i.intValue() + 1);
            }
        }

        // Retrieve feed from store
        //IFeed retrievedFeed = iFeedService.getIFeed(listId);

        if (retrievedFeed == null) {
            // Throw 404 if the feed doesn't exist
            throw new ResourceNotFoundException();
        }

        IFeedSolrQuery solrQuery = new IFeedSolrQuery(solrServer, iFeedService);
        solrQuery.setRows(501);
        solrQuery.setShowDebugInfo(true);

        List<Map<String, Object>> result = solrQuery.getIFeedResults(retrievedFeed, sortField,
                getEnum(SortDirection.class, sortDirection));

        if (startBy != null && endBy != null) {
            if (result.size() < startBy) {
                startBy = result.size();
            }
            if (result.size() < endBy) {
                endBy = result.size();
            }
            result = result.subList(startBy, endBy);
        }

        model.addAttribute("result", result);
        //model.addAttribute("feed", retrievedFeed);
        Util.setLocalValue(retrievedFeed);
        return "documentList";
    }


    @RequestMapping(value = "/documents/{documentId}")
    public String detailsHtml(@PathVariable String documentId, Model model) {
        return details(documentId, model);
    }

    @RequestMapping(value = "/documents/metadata")
    public String detailsByRequestParam(@RequestParam(value = "documentId", required = false) String documentId,
                                        Model model, HttpServletResponse response) {
        if (documentId == null) {
            throw new BadRequestException("Document id must not be null.");
        }
        return details(documentId, model);
    }

    @RequestMapping(value = "/documents/{documentId}/metadata")
    public String details(@PathVariable String documentId, Model model) {
        // We are flexible here; "workspace://SpacesStore/" is added if it isn't provided and vice versa.
        String fullId;
        if (documentId.contains("workspace://SpacesStore/")) {
            fullId = documentId;
        } else {
            fullId = "workspace://SpacesStore/" + documentId;
        }
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(fullId);

        SimpleDateFormat inParser = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat outFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("sv", "SE"));

        Map<String, String> idValueMap = new HashMap<String, String>();
        List<FieldsInf> infs = iFeedService.getFieldsInfs(); // todo cache?
        List<FieldInf> fieldInfs = null;
        if (infs.size() > 0) {
            fieldInfs = infs.get(infs.size() - 1).getFieldInfs();
            for (FieldInf fieldInf : fieldInfs) {
                for (FieldInf child : fieldInf.getChildren()) {
                    String childId = child.getId();
                    if (StringUtils.hasText(childId)) { // If it has an id it is not just a caption
                        // Does the document have any value for this field?
                        Object value = documentInfo.getMetadata().get(childId);
                        if (value instanceof String) {
                            String sValue = (String) value;
                            if (StringUtils.hasText(sValue)) {
                                try {
                                    Calendar calendar = Calendar.getInstance();
                                    Date date = inParser.parse(sValue);
                                    calendar.setTime(date);
                                    outFormatter.setTimeZone(inParser.getTimeZone());
                                    idValueMap.put(childId, outFormatter.format(date));
                                } catch (ParseException e) {
                                    // Wasn't a date
                                    idValueMap.put(childId, sValue);
                                }
                            }
                        } else if (value instanceof Collection) {
                            String sValue = collectionToString((Collection) value);
                            if (StringUtils.hasText(sValue)) {
                                idValueMap.put(childId, sValue);
                            }
                        }
                    }
                }
            }
        }

        model.addAttribute("fieldInfs", fieldInfs);
        model.addAttribute("idValueMap", idValueMap);

        return "documentDetails";
    }

    private String collectionToString(Collection values) {
        if (values.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator iterator = values.iterator();
        Object firstElement = iterator.next();
        sb.append(firstElement.toString());

        while (iterator.hasNext()) {
            sb.append(", " + iterator.next().toString());
        }

        return sb.toString();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) {
            super(msg);
        }
    }

    @ExceptionHandler(IFeedServiceException.class)
    public ModelAndView handleAlfrescoDocumentServiceException(Exception e) {
        if (!(e instanceof IFeedServiceException)) {
            LOGGER.error("IFeed Excption: {}", e);
            e = new IFeedServiceException("error.unhandled", "Internal IFeed Error", e);
        }
        return new ModelAndView("ExceptionHandler", Collections.singletonMap("exception", e));
    }
}
