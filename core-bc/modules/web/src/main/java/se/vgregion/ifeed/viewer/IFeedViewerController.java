package se.vgregion.ifeed.viewer;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;
import se.vgregion.ifeed.service.exceptions.IFeedServiceException;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static se.vgregion.common.utils.CommonUtils.getEnum;

/**
 * Controller to view feeds.
 */

@Controller
public class IFeedViewerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedViewerController.class);

    private IFeedService iFeedService;
    private SolrServer solrServer;
    private AlfrescoDocumentService alfrescoMetadataService;

    /**
     * Creates an instance.
     *
     * @param iFeedService    is used to get data from the data layer.
     * @param documentService is used to get data from alfresco.
     * @param solrServer      to access the solr server.
     */
    @Autowired
    public IFeedViewerController(IFeedService iFeedService, AlfrescoDocumentService documentService,
                                 SolrServer solrServer) {
        this.solrServer = solrServer;
        this.iFeedService = iFeedService;
        this.alfrescoMetadataService = documentService;
    }

    /**
     * Rendering controller method for viewing a feed in the 'plain' html view.
     *
     * @param listId        the id of the feed to be viewed. Must be present in the db.
     * @param model         where to put the data that is to be presented to the user.
     * @param sortField     what field to used for sorting.
     * @param sortDirection what direction to use when sorting, sould be asc or desc.
     * @return what jsp to be used for rendering.
     */
    @RequestMapping(value = "/documentlists/{listId}")
    public String getIFeedHtml(@PathVariable Long listId, Model model,
                               @RequestParam(value = "by", required = false) String sortField,
                               @RequestParam(value = "dir", required = false) String sortDirection) {
        return getIFeedById(listId, model, sortField, sortDirection, null, null, null);
    }

    public static final Map<String, Integer> callsToJsonpMetadata = new TreeMap<String, Integer>();

    /**
     * Controller method to view a list of url:s (or some other id:s) from where the json-data have been fetched
     * previously.
     *
     * @param model where to put the data to be displayed.
     * @return what jsp to use as view.
     */
    @RequestMapping(value = "/metadata-calls")
    public String viewCallsToJsonpMetadata(Model model) {
        model.addAttribute("keys", callsToJsonpMetadata.keySet());
        model.addAttribute("values", callsToJsonpMetadata);
        return "metadata-calls";
    }

    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Controller method to be able to get feeds by calling the application with pure GET-http - using only parameters
     * (not path-parameters) for the call.
     *
     * @param instance      the id or the serialized instance of an IFeed object.
     * @param model         where to put the data that is to be presented to the user.
     * @param sortField     the field to be used to sort the result.
     * @param sortDirection what direction to use when sorting, sould be asc or desc.
     * @param startBy       offset in the db (solr-server) result.
     * @param endBy         the end of the span of results.
     * @param fromPage      should be the url (or some other id, perhaps) of the page that calls the function via the
     *                      ajax script.
     * @return the result of the opration.
     */
    @RequestMapping(value = "/meta", method = {RequestMethod.POST, RequestMethod.GET})
    public String getIFeedByParams(@RequestParam(value = "instance") String instance, Model model,
                                   @RequestParam(value = "by", required = false) String sortField,
                                   @RequestParam(value = "dir", required = false) String sortDirection,
                                   @RequestParam(value = "startBy", required = false) Integer startBy,
                                   @RequestParam(value = "endBy", required = false) Integer endBy,
                                   @RequestParam(value = "fromPage", required = false) String fromPage) {
        long timeBefore = System.currentTimeMillis();
        String result = getIFeed(instance, model, sortField, sortDirection, startBy, endBy, fromPage);
        // System.out.println("Time for call was " + (System.currentTimeMillis() - timeBefore) + "ms. Instance " + instance + " from page " + fromPage);
        return result;
    }

    /**
     * Gets the ifeed from either a db or from a plain instance (sent as text).
     *
     * @param listIdOrSerializedInstance the id of the feed in the db or the feed itself as an instance. To bad that
     *                                   spring portlet mvc does not function well enough to work for the latter
     *                                   scenario. It is kept in case this is
     *                                   fixed later.
     * @param model                      to place the data for the view.
     * @param sortField                  the field to be used to sort the result.
     * @param sortDirection              what direction to use when sorting, sould be asc or desc.
     * @param startBy                    what offset to have in the result from the db.
     * @param endBy                      where to end the result (truncate it) from the db.
     * @param fromPage                   should be the url (or some other id, perhaps) of the page that calls the
     *                                   function via the
     *                                   ajax script.
     * @return
     */
    @RequestMapping(value = "/documentlists/{listIdOrSerializedInstance}/metadata")
    public String getIFeed(@PathVariable String listIdOrSerializedInstance, Model model,
                           @RequestParam(value = "by", required = false) String sortField,
                           @RequestParam(value = "dir", required = false) String sortDirection,
                           @RequestParam(value = "startBy", required = false) Integer startBy,
                           @RequestParam(value = "endBy", required = false) Integer endBy,
                           @RequestParam(value = "fromPage", required = false) String fromPage) {
        if (isNumeric(listIdOrSerializedInstance)) {
            Long id = Long.parseLong(listIdOrSerializedInstance);
            return getIFeedById(id, model, sortField, sortDirection, startBy, endBy, fromPage);
        } else {
            IFeed ifeed = IFeed.fromJson(listIdOrSerializedInstance);
            return getIFeedByInstance(ifeed, model, sortField, sortDirection, startBy, endBy, fromPage);
        }
    }

    /**
     * Gets the ifeed from either a db or from a plain instance (sent as text).
     *
     * @param instance      the id of the feed in the db or the feed itself as an instance. To bad that
     *                      spring portlet mvc does not function well enough to work for the latter
     *                      scenario. It is kept in case this is
     *                      fixed later.
     * @param model         to place the data for the view.
     * @param sortField     the field to be used to sort the result.
     * @param sortDirection what direction to use when sorting, sould be asc or desc.
     * @param startBy       what offset to have in the result from the db.
     * @param endBy         where to end the result (truncate it) from the db.
     * @param fromPage      should be the url (or some other id, perhaps) of the page that calls the
     *                      function via the
     *                      ajax script.
     * @param response      handle to write the result somewhere.
     * @return
     */
    @RequestMapping(value = "/metaascsv", produces = {"text/csv"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String getIFeedAsCsv(@RequestParam(value = "instance") String instance, Model model,
                                @RequestParam(value = "by", required = false) String sortField,
                                @RequestParam(value = "dir", required = false) String sortDirection,
                                @RequestParam(value = "startBy", required = false) Integer startBy,
                                @RequestParam(value = "endBy", required = false) Integer endBy,
                                @RequestParam(value = "fromPage", required = false) String fromPage,
                                HttpServletResponse response) {
        //public void exportCsv(@PathVariable String listIdOrSerializedInstance, HttpServletRequest request, HttpServletResponse  response) {
        String url;
        List<Map<String, Object>> resultAccumulator = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> oneIterationResult = new ArrayList<Map<String, Object>>();

        if (endBy != null) {
            throw new RuntimeException("Did´nt think this would happen!");
        }

        if (startBy == null) {
            startBy = 0;
        }
        endBy = startBy + 500;
        do {
            oneIterationResult.clear();
            if (isNumeric(instance)) {
                Long id = Long.parseLong(instance);
                url = getIFeedById(id, model, sortField, sortDirection, startBy, endBy, fromPage);
            } else {
                try {
                    instance = URLDecoder.decode(instance, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                IFeed ifeed = IFeed.fromJson(instance);
                url = getIFeedByInstance(ifeed, model, sortField, sortDirection, startBy, endBy, fromPage);
            }
            List<Map<String, Object>> result = (List<Map<String, Object>>) model.asMap().get("result");
            oneIterationResult.addAll(result);
            resultAccumulator.addAll(result);
            startBy += 500;
            endBy = startBy + 500;
        } while (!oneIterationResult.isEmpty());

        BufferedOutputStream bos = null;
        OutputStream portletOutputStream = null;
        try {
            portletOutputStream = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            bos = new BufferedOutputStream(portletOutputStream);

            //List<Map<String, Object>> result = (List<Map<String, Object>>) model.asMap().get("result");

            List<FieldInf> fields = iFeedService.getFieldInfs();

            bos.write(0xEF);
            bos.write(0xBB);
            bos.write(0xBF);

            if (!resultAccumulator.isEmpty()) {
                for (FieldInf fi : fields) {
                    if (fi.isInHtmlView()) {
                        for (FieldInf child : fi.getChildren()) {
                            if (child.isInHtmlView()) {
                                bos.write(prettifyFeedValue(child.getName()));
                                bos.write(";".getBytes());
                            }
                        }
                    }
                }
                bos.write("\n".getBytes());
                for (Map<String, Object> item : resultAccumulator) {
                    for (FieldInf fi : fields) {
                        if (fi.isInHtmlView()) {
                            for (FieldInf child : fi.getChildren()) {
                                if (child.isInHtmlView()) {
                                    bos.write(prettifyFeedValue(item.get(child.getId()) + ""));
                                    bos.write(";".getBytes());
                                }
                            }
                        }
                    }
                    bos.write("\n".getBytes());
                }
            }

            bos.close();
            portletOutputStream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return url;
    }

    private byte[] prettifyFeedValue(String value) {
        if ("null".equals(value)) {
            return "".getBytes();
        }
        value = value.replaceAll(Pattern.quote("["), "");
        value = value.replaceAll(Pattern.quote("]"), "");
        value = value.replaceAll(Pattern.quote("\""), "\"\"");
        value = '"' + value + '"';
        return value.getBytes();
    }


    /**
     * Gets an ifeed from the db for the purpose of displaying it to the user.
     *
     * @param listId        the id of the feed to be viewed. Must be present in the db.
     * @param model         where to put the data that is to be presented to the user.
     * @param sortField     the field to be used to sort the result.
     * @param sortDirection what direction to use when sorting, sould be asc or desc.
     * @param startBy       offset in the db (solr-server) result.
     * @param endBy         the end of the span of results.
     * @param fromPage      should be the url (or some other id, perhaps) of the page that calls the function via the
     *                      ajax script.
     * @return what jsp to use when showing the data.
     */
    public String getIFeedById(@PathVariable Long listId, Model model,
                               @RequestParam(value = "by", required = false) String sortField,
                               @RequestParam(value = "dir", required = false) String sortDirection,
                               @RequestParam(value = "startBy", required = false) Integer startBy,
                               @RequestParam(value = "endBy", required = false) Integer endBy,
                               @RequestParam(value = "fromPage", required = false) String fromPage) {
        IFeed retrievedFeed = iFeedService.getIFeed(listId);
        if (retrievedFeed == null) {
            LOGGER.error("Did not find feed with id " + listId + " on page " + fromPage);
            throw new ResourceNotFoundException();
        }
        return getIFeedByInstance(retrievedFeed, model, sortField, sortDirection, startBy, endBy, fromPage);
    }

    /**
     * Controller method to load and display an ifeed.
     *
     * @param retrievedFeed the ifeed to be displayed.
     * @param model         data to be used in rendering the data.
     * @param sortField     what field to use as sorting.
     * @param sortDirection what direction to use when sorting. Should be asc or desc.
     * @param startBy       offset in the db (solr-server) result.
     * @param endBy         the end of the span of results.
     * @param fromPage      should be the url (or some other id, perhaps) of the page that calls the function via the
     *                      ajax script.
     * @return what jsp to use...
     */
    public String getIFeedByInstance(IFeed retrievedFeed, Model model,
                                     String sortField,
                                     String sortDirection,
                                     Integer startBy,
                                     Integer endBy,
                                     String fromPage) {
        Util.setLocalValue(retrievedFeed);

        if (fromPage != null && !"".equals(fromPage.trim())) {
            Integer i = callsToJsonpMetadata.get(fromPage);
            if (i == null) {
                callsToJsonpMetadata.put(fromPage, 1);
            } else {
                callsToJsonpMetadata.put(fromPage, i.intValue() + 1);
            }
        }

        if (retrievedFeed == null) {
            // Throw 404 if the feed doesn't exist
            throw new ResourceNotFoundException();
        }

        IFeedSolrQuery solrQuery = new IFeedSolrQuery(solrServer, iFeedService);
        if (startBy != null && startBy >= 0) {
            solrQuery.setStart(startBy);
            if (endBy != null && endBy > startBy) {
                solrQuery.setRows(endBy - startBy);
            }
        } else {
            solrQuery.setRows(25000);
        }

        solrQuery.setShowDebugInfo(true);

        List<Map<String, Object>> result = solrQuery.getIFeedResults(retrievedFeed, sortField,
                getEnum(SortDirection.class, sortDirection));

        model.addAttribute("result", result);
        return "documentList";
    }

    /**
     * Showing detial for an document.
     *
     * @param documentId thw id of the document (in alfresco).
     * @param model      where to put the data that is to be presented to the user.
     * @return what jsp to use as view.
     */
    @RequestMapping(value = "/documents/{documentId}")
    public String detailsHtml(@PathVariable String documentId, Model model) {
        return details(documentId, model);
    }

    /**
     * Used to get document information with GET-html parameters (as oposed to with path parameters).
     *
     * @param documentId thw id of the document (in alfresco).
     * @param model      data to be used in rendering the data.
     * @param response   to write the information to.
     * @return what jsp to use.
     */
    @RequestMapping(value = "/documents/metadata")
    public String detailsByRequestParam(@RequestParam(value = "documentId", required = false) String documentId,
                                        Model model, HttpServletResponse response) {
        if (documentId == null) {
            throw new BadRequestException("Document id must not be null.");
        }
        return details(documentId, model);
    }

    static final Pattern ISO8601 = Pattern.compile("^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]+)?(Z|[+-](?:2[0-3]|[0-1][0-9]):[0-5][0-9])?$");

    static boolean itsSomeDateValue(String v) {
        return ISO8601.matcher(v).matches();
    }

    static SimpleDateFormat sdfForView = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);

    static SimpleDateFormat sdfForDataText = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.GERMAN);

    @Deprecated
    static Calendar toDateOld(String sValue) {
        SimpleDateFormat inParser = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        //if (StringUtils.hasText(sValue)) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = inParser.parse(sValue);
            calendar.setTime(date);
            return calendar;
            //outFormatter.setTimeZone(inParser.getTimeZone());
            //idValueMap.put(childId, outFormatter.format(date));
        } catch (ParseException e) {
            // Wasn't a date
            //idValueMap.put(childId, sValue);
            throw new RuntimeException(e);
        }
        //}
    }

    static String toTextDate(String iso8601format) {
        try {
            return toTextDateImpl(iso8601format);
        } catch (ParseException e) {
            return iso8601format;
        }
    }

    static String toTextDateImpl(String iso8601format) throws ParseException {
        //String string = "2015-04-10T08:34:00.000Z";
        //String defaultTimezone = TimeZone.getDefault().getID();
        Date date = sdfForDataText.parse(iso8601format.replaceAll("Z$", "+0000"));
        return sdfForView.format(date);
    }

    /**
     * Using path variables to asscess a feed from a http client.
     *
     * @param documentId thw id of the document (in alfresco).
     * @param model      data to be used in rendering the data.
     * @return what jsp to use.
     */
    @RequestMapping(value = "/documents/{documentId}/metadata")
    public String details(@PathVariable String documentId, Model model) {
        if (documentId.startsWith("[")) {
            if (documentId.endsWith("]")) {
                documentId = documentId.substring(1, documentId.length() - 1);
            } else {
                throw new RuntimeException("Strange document id " + documentId);
            }
        }
        // We are flexible here; "workspace://SpacesStore/" is added if it isn't provided and vice versa.
        String fullId;
        if (documentId.contains("workspace://SpacesStore/")) {
            fullId = documentId;
        } else {
            fullId = "workspace://SpacesStore/" + documentId;
        }
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(fullId);

        //SimpleDateFormat inParser = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
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
                            if (itsSomeDateValue(sValue)) {
                                sValue = toTextDate(sValue);
                            }
                            idValueMap.put(childId, sValue);
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

    /**
     * To signal when an feed is not found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /**
     * To signal a http bad request.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) {
            super(msg);
        }
    }

    /**
     * Handling exceptions occuring when http clients calls controller methods in this class.
     *
     * @param e the error to handle.
     * @return where to steer the view.
     */
    @ExceptionHandler(IFeedServiceException.class)
    public ModelAndView handleAlfrescoDocumentServiceException(Exception e) {
        if (!(e instanceof IFeedServiceException)) {
            LOGGER.error("IFeed Excption: {}", e);
            e = new IFeedServiceException("error.unhandled", "Internal IFeed Error", e);
        }
        return new ModelAndView("ExceptionHandler", Collections.singletonMap("exception", e));
    }
}
