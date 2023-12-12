package se.vgregion.ifeed.viewer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;
import se.vgregion.ifeed.service.exceptions.IFeedServiceException;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.ifeed.ObjectRepo;
import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.OldIndexData;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.types.*;
import se.vgregion.ldap.LdapApi;
import se.vgregion.ldap.LdapSupportService;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static se.vgregion.ifeed.service.ifeed.IFeedService.toTableMarkup;
import static se.vgregion.ifeed.viewer.Column.toColumns;

/**
 * Controller to view feeds.
 */

@Controller
@Transactional
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
        return getIFeedById(listId, model, sortField, sortDirection, null, null, null, null);
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

    private String[] toStringArray(MultiValueMap<String, String> map, String key) {
        if (map == null)
            return null;
        List<String> list = map.get(key);
        if (list == null || list.isEmpty())
            return null;
        return list.toArray(new String[list.size()]);
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
    public List<Map> getIFeedByParams(@RequestParam(value = "instance") String instance, Model model,
                                      @RequestParam(value = "by", required = false) String sortField,
                                      @RequestParam(value = "dir", required = false) String sortDirection,
                                      @RequestParam(value = "startBy", required = false) Integer startBy,
                                      @RequestParam(value = "endBy", required = false) Integer endBy,
                                      @RequestParam(value = "fromPage", required = false) String fromPage,
                                      @RequestParam(value = "f", required = false) String[] f
    ) {
        //String[] f = toStringArray(allRequestParams, "f");
        long timeBefore = System.currentTimeMillis();
        List<Map> result = getIFeed(instance, model, sortField, sortDirection, startBy, endBy, fromPage, f/*, allRequestParams*/);
        // System.out.println("Time for call was " + (System.currentTimeMillis() - timeBefore) + "ms. Instance " + instance + " from page " + fromPage);
        return result;
    }

    /*static boolean isValid(Charset charset, String data) {
        try {
            charset.decode(
                    ByteBuffer.wrap(data.getBytes()));
        } catch (Exception ex) {
            return false;
        }
        return true;
    }*/

    @RequestMapping(value = "/documentlists/{listIdOrSerializedInstance}/metadata.json")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody()
    @Produces(value = "application/json;charset=UTF-8")
    public List<Map> getIFeed(@PathVariable String listIdOrSerializedInstance, Model model,
                              @RequestParam(value = "by", required = false) String sortField,
                              @RequestParam(value = "dir", required = false) String sortDirection,
                              @RequestParam(value = "startBy", required = false) Integer startBy,
                              @RequestParam(value = "endBy", required = false) Integer endBy,
                              @RequestParam(value = "fromPage", required = false) String fromPage,
                              @RequestParam(value = "f", required = false) String[] f/*,
                         @RequestParam(value = "f") MultiValueMap<String, String> allRequestParams*/) {
        //String[] f = toStringArray(allRequestParams, "f");
        // if (true) throw new RuntimeException("getIFeed");
        if (isNumeric(listIdOrSerializedInstance)) {
            Long id = Long.parseLong(listIdOrSerializedInstance);
            getIFeedById(id, model, sortField, sortDirection, startBy, endBy, fromPage, f/*, allRequestParams*/);
        } else {
            IFeed ifeed = IFeed.fromJson(listIdOrSerializedInstance);
            getIFeedByInstance(ifeed, model, sortField, sortDirection, startBy, endBy, fromPage, null/*, f*/);
        }

        //String result = new String(gson.toJson(model.asMap().get("result")).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String result = new String(gson.toJson(model.asMap().get("result")));

        List<Map> maps = (List<Map>) model.asMap().get("result");
/*
        for (Map map : maps) {

            String title = (String) map.get("title");
            title = title.toLowerCase(Locale.ROOT);
            if (title.contains("å") || title.contains("ä") || title.contains("ö")) {
                System.out.println("Hittade " + title);
            } else {
                System.out.println("Hittade inte " + title);
            }

        }
*/
        /*System.out.println(isValid(StandardCharsets.UTF_8, result));
        System.out.println(isValid(StandardCharsets.UTF_16, result));
        System.out.println(isValid(StandardCharsets.ISO_8859_1, result));
        System.out.println(isValid(StandardCharsets.US_ASCII, result));*/

        return maps;
        // return model.asMap().get("result");
        //return gson.toJson(model.asMap().get("result"));
        // return "thisDoesNotMatherItSeems";
    }

    // Todo: user fix problem with ResponseBody and use that instead.
    public static ThreadLocal<String> jsonResult = new ThreadLocal<>();

    private final static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

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
                                @RequestParam(value = "f", required = false) String[] f,
                                //@RequestParam(value = "f") MultiValueMap<String, String> allRequestParams,
                                HttpServletResponse response) {
        //String[] f = toStringArray(allRequestParams, "f");
        //public void exportCsv(@PathVariable String listIdOrSerializedInstance, HttpServletRequest request, HttpServletResponse  response) {
        String url;
        Set<Map<String, Object>> resultAccumulator = new HashSet<>();
        Set<Map<String, Object>> oneIterationResult = new HashSet<>();

        if (endBy != null) {
            throw new RuntimeException("Did´nt think this would happen!");
        }

        if (startBy == null) {
            startBy = 0;
        }
        endBy = startBy + 500;
        endBy = 1_000_000;
        //do {
        oneIterationResult.clear();
        if (isNumeric(instance)) {
            Long id = Long.parseLong(instance);
            url = getIFeedById(id, model, sortField, sortDirection, startBy, endBy, fromPage, f/*, allRequestParams*/);
        } else {
            try {
                instance = URLDecoder.decode(instance, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            IFeed ifeed = IFeed.fromJson(instance);
            url = getIFeedByInstance(ifeed, model, sortField, sortDirection, startBy, endBy, fromPage, f/*, f*/);
        }
        List<Map<String, Object>> result = (List<Map<String, Object>>) model.asMap().get("result");
        oneIterationResult.addAll(result);
        resultAccumulator.addAll(result);
        startBy += 500;
        endBy = startBy + 500;
        //} while (!oneIterationResult.isEmpty());

        BufferedOutputStream bos = null;
        OutputStream portletOutputStream = null;
        try {
            portletOutputStream = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            bos = new BufferedOutputStream(portletOutputStream);

            //List<Map<String, Object>> result = (List<Map<String, Object>>) model.asMap().get("result");

            bos.write(0xEF);
            bos.write(0xBB);
            bos.write(0xBF);

            NavigableSet<String> keys = new TreeSet<>();
            for (Map<String, Object> item : resultAccumulator) {
                for (String s : item.keySet()) {
                    if (item.get(s) != null && !"".equals(item.get(s).toString().trim())) {
                        keys.add(s);
                    }
                }
            }

            if (!resultAccumulator.isEmpty()) {
                /*for (LabelledValue labelledValue : newAlfrescoBariumDisplayFieldsWithoutValue()) {
                    if (keys.contains(labelledValue.key)) {
                        bos.write(prettifyFeedValue(labelledValue.getLabel()));
                        bos.write(";".getBytes());
                    }
                }*/
                for (LabelledValue labelledValue : newSofiaDisplayFieldsWithoutValue()) {
                    if (keys.contains(labelledValue.key)) {
                        bos.write(prettifyFeedValue(labelledValue.getLabel()));
                        bos.write(";".getBytes());
                    }
                }

                for (Map<String, Object> item : resultAccumulator) {
                    bos.write("\n".getBytes());
/*
                    for (LabelledValue labelledValue : newAlfrescoBariumDisplayFieldsWithoutValue()) {
                        if (keys.contains(labelledValue.key)) {
                            bos.write(prettifyFeedValue(String.valueOf(item.get(labelledValue.getKey()))));
                            bos.write(";".getBytes());
                        }
                    }
*/
                    for (LabelledValue labelledValue : newSofiaDisplayFieldsWithoutValue()) {
                        if (keys.contains(labelledValue.key)) {
                            bos.write(prettifyFeedValue(String.valueOf(item.get(labelledValue.getKey()))));
                            bos.write(";".getBytes());
                        }
                    }
                }
            }

/*
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
*/

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
                               @RequestParam(value = "fromPage", required = false) String fromPage,
                               @RequestParam(value = "f") String[] f/*,
                             @RequestParam(value = "f") MultiValueMap<String, String> allRequestParams*/) {
        //String[] f = toStringArray(allRequestParams, "f");
        // IFeed retrievedFeed = iFeedService.getIFeed(listId);
        IFeed retrievedFeed = iFeedService.getFeedForSolrQuery(listId);
        retrievedFeed.getFilters().forEach(fo -> fo.getMetadata());
        if (retrievedFeed == null) {
            LOGGER.error("Did not find feed with id " + listId + " on page " + fromPage);
            throw new ResourceNotFoundException();
        }

        String result = getIFeedByInstance(retrievedFeed, model, sortField, sortDirection, startBy, endBy, fromPage, f);

        return result;
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
    String getIFeedByInstance(IFeed retrievedFeed, Model model,
                              String sortField,
                              String sortDirection,
                              Integer startBy,
                              Integer endBy,
                              String fromPage,
                              String[] fieldToSelect) {

        if (retrievedFeed == null) {
            // Throw 404 if the feed doesn't exist
            throw new ResourceNotFoundException();
        }
        if (sortField == null || sortField.isEmpty()) sortField = IFeedSolrQuery.DEFAULT_SORT_FIELD;
        if (sortDirection == null || sortDirection.isEmpty())
            sortDirection = IFeedSolrQuery.DEFAULT_SORT_DIRECTION.toString();


        if (sortField == null || sortField.trim().isEmpty() || "null".equalsIgnoreCase(sortField) /*|| "dc.title".equalsIgnoreCase(sortField)*/) {
            // sortField = "dc.title";
            sortField = "title";
        }

        if (startBy == null) {
            startBy = 0;
        }

        if (endBy == null) {
            endBy = 1_000_000;
        }

        FieldInf field = iFeedService.getFieldInf(sortField);
        /*if (fieldToSelect != null)
            System.out.println("Fields to select: " + new ArrayList<>(Arrays.asList(fieldToSelect)));
        else
            System.out.println("Fields not set.");*/
        Result result = client.query(retrievedFeed.toQuery(client.fetchFields()), startBy, endBy, sortDirection, field, fieldToSelect);

        if (result != null && result.getDocumentList() != null && result.getDocumentList().getDocuments() != null
                && fieldToSelect != null && fieldToSelect.length > 0) {
            Set<String> keys = new HashSet<>(Arrays.asList(fieldToSelect));
            for (Map<String, Object> item : result.getDocumentList().getDocuments()) {
                item.keySet().retainAll(keys);
            }
        }

        if (retrievedFeed.getLinkNativeDocument()) {
            for (Map<String, Object> item : result.getDocumentList().getDocuments()) {
                String originalDownloadLatestVersionUrl = (String) item.get("originalDownloadLatestVersionUrl");
                if (originalDownloadLatestVersionUrl != null && !"".equals(originalDownloadLatestVersionUrl.trim())) {
                    // Is a Sofia doc.
                    item.put("url", originalDownloadLatestVersionUrl);
                } else {
                    // Assume it is from Barium or Alfresco.
                    item.put("url", item.get("dc.identifier.native"));
                }
            }
        }

        // List<Map<String, Object>> result = ("asc".equalsIgnoreCase(sortDirection)) ? sorter.getSortedValues() : sorter.getReversedSortedValues();
        // model.addAttribute("result", otherResult.getResponse().getDocs());
        // result = result.subList(startBy, endBy);
        // model.addAttribute("result", result);
        if (result.getDocumentList() == null || result.getDocumentList().getDocuments() == null) {
            model.addAttribute("result", new ArrayList<>());
            // model.addAttribute(new ArrayList<>());
        } else {
            model.addAttribute("result", result.getDocumentList().getDocuments());
            // model.addAttribute(result.getResponse().getDocs());
        }
        model.addAttribute("query", client.getLatestCallAsGet());
        return "documentList";
    }

    SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    private class SortingMap extends TreeMap<Comparable, List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> get(Object key) {
            if (!containsKey(key)) {
                put((Comparable) key, new ArrayList<>());
            }
            return super.get(key);
        }

        List<Map<String, Object>> getSortedValues() {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Comparable comparable : SortingMap.this.keySet()) {
                result.addAll(SortingMap.this.get(comparable));
            }
            return result;
        }

        List<Map<String, Object>> getReversedSortedValues() {
            List<Map<String, Object>> result = getSortedValues();
            Collections.reverse(result);
            return result;
        }

    }

    /**
     * There are other names for the fields in the SOFIA-specification. That could lead to strange behavior for the
     * script-rendered lists on the pages - that is if those lists where using some of the old fields and got a null /
     * blank in response. To mitigate that - copy values into the fields that might be empty.
     * Here is a list of used fields from 20181009:
     * <code>
     * dc.contributor.acceptedby 1
     * dc.creator.document 12
     * dc.creator.freetext 1
     * dc.creator.function 10
     * dc.date.issued 157
     * dc.date.saved 19
     * dc.date.validfrom 67
     * dc.date.validto 175
     * dc.description 42
     * dc.publisher.forunit 74
     * dc.publisher.forunit.flat 2
     * dc.relation.replaces 17
     * dc.title 3793
     * dc.title.filename 1
     * dc.type.document.id 1
     * dc.type.document.serie 5
     * dc.type.document.structure 301
     * title 4
     * </code>
     * The number after the field is the count, number of times the field exists in various lists.
     *
     * @param inThatItem
     */
    private void copyValueFromSofiaToAlfrescoBariumFields(Map<String, Object> inThatItem) {
        putValueInsideWhenTargetIsNull("title", "dc.title", inThatItem);
        putValueInsideWhenTargetIsNull("vgr:VgrExtension.vgr:CreatedBy", "dc.creator.freetext", inThatItem);

        putValueInsideWhenTargetIsNull("core:ArchivalObject.core:CreatedDateTime", "dc.date.issued", inThatItem);
        putValueInsideWhenTargetIsNull("core:ArchivalObject.core:CreatedDateTime", "dc.date.saved", inThatItem);
        putValueInsideWhenTargetIsNull("core:ArchivalObject.core:Description", "dc.description", inThatItem);
        putValueInsideWhenTargetIsNull("vgr:VgrExtension.vgr:PublishedForUnit", "dc.publisher.forunit", inThatItem);

        putValueInsideWhenTargetIsNull("vgr:VgrExtension.vgr:PublishedForUnit", "dc.publisher.forunit.flat", inThatItem);

        putValueInsideWhenTargetIsNullAndValueExistInOldIndex(
                "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification",
                "dc.type.document.serie",
                inThatItem,
                OldIndexData.getCachedAlfrescoBariumValues().get("dc.type.document.serie")
        );

        /*putValueInsideWhenTargetIsNullAndValueExistInOldIndex(
                "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification",
                "dc.type.document.structure",
                inThatItem,
                OldIndexData.getCachedAlfrescoBariumValues().get("dc.type.document.structure")
        );*/
    }

    private void putValueInsideWhenTargetIsNullAndValueExistInOldIndex(String sourceKey, String targetKey, Map<String, Object> inThatItem, List<Object> withThosePossibleValues) {
        List targetValue = (List) inThatItem.get(targetKey);
        if (targetValue == null) {
            targetValue = new ArrayList();
            inThatItem.put(targetKey, targetValue);
        }
        List sourceValue = (List) inThatItem.get(sourceKey);

        if (sourceValue == null) {
            return;
        }

        if (sourceValue.isEmpty() || targetValue.equals(sourceValue)) {
            return;
        }

        for (Object possibleValue : withThosePossibleValues) {
            if (sourceValue.contains(possibleValue) && !targetValue.contains(possibleValue)) {
                targetValue.add(possibleValue);
            }
        }
    }


    private void putValueInsideWhenTargetIsNull(String sourceKey, String targetKey, Map<String, Object> inThatItem) {
        Object targetValue = inThatItem.get(targetKey);
        if (targetValue == null || targetValue.toString().trim().isEmpty()) {
            Object sourceValue = inThatItem.get(sourceKey);
            if (sourceValue != null && !sourceValue.toString().trim().isEmpty()) {
                inThatItem.put(targetKey, sourceValue);
            }
        }
    }


    /**
     * Showing detial for an document.
     *
     * @param documentId thw id of the document (in alfresco).
     * @param model      where to put the data that is to be presented to the user.
     * @return what jsp to use as view.
     */
    @RequestMapping(value = "/documents/{documentId}")
    public String detailsHtml(@PathVariable String documentId, Model model, HttpServletResponse response,
                              @RequestParam(value = "type", required = false) String type) {
        return details(documentId, model, response);
    }

    public String details(String documentId, Model model, HttpServletResponse response) {
        return details(documentId, model, response, null);
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
        return details(documentId, model, response);
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
            //idValueMap.put(childId, outFormatter.ifHsaThenFormat(date));
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

    //static List<String> sofiaSystems = new ArrayList<>(Arrays.asList("SOFIA", "SISOM"));

    private static LdapApi ldapApi = LdapApi.newInstanceFromConfig();

    @Autowired
    @Deprecated
    private LdapSupportService ldapSupportService;

    @Autowired
    private LdapPersonService ldapPersonService;

    private String ifHsaThenFormat(String that) {
        if (that.equals("SE2321000131-E000000000001")) {
            that = "Västra Götalandsregionen (SE2321000131-E000000000001)";
            return that;
        }
        if (that.matches("SE[0-9]{10}\\-[A-Z][0-9]{12}")) {
            List<Map<String, Object>> items = ldapApi.query(String.format("(hsaIdentity=%s)", that));
            if (items.size() == 1) {
                Map<String, Object> item = items.get(0);
                if (item.containsKey("ou")) {
                    that = String.format("%s (%s)", item.get("ou"), that);
                } else {
                    that = String.format("%s (%s)", item.get("ouShort"), that);
                }
            }
        }
        return that;
    }

    private Object ifHsaThenFormat(Object that) {
        if (that instanceof List) {
            List<String> texts = new ArrayList<>();
            for (Object o : ((List) that)) {
                texts.add(ifHsaThenFormat(o.toString()));
            }
            return texts;
        } else {
            return ifHsaThenFormat(that.toString());
        }
    }

    /**
     * <div class="ifeedDocList"
     * columnes="dc.title|Titel (autokomplettering)|left|70"
     * fontsize="auto"
     * defaultsortcolumn="dc.title"
     * defaultsortorder="desc"
     * showtableheader="yes"
     * linkoriginaldoc="no"
     * limit="0"
     * hiderightcolumn="no"
     * feedid="3331656"
     * data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
     * </div>
     *
     * @return
     */

    @RequestMapping(value = "/display")
    public String display(@RequestParam String columnes,
                          @RequestParam(defaultValue = "auto", required = false) String fontsize,
                          @RequestParam(defaultValue = "title", required = false) String defaultsortcolumn,
                          @RequestParam(defaultValue = "asc", required = false) String defaultsortorder,
                          @RequestParam(defaultValue = "yes", required = false) String showtableheader,
                          @RequestParam(defaultValue = "no", required = false) String linkoriginaldoc,
                          @RequestParam(defaultValue = "0", required = false) Integer limit,
                          @RequestParam(defaultValue = "no", required = false) String hiderightcolumn,
                          @RequestParam String feedid,
                          @RequestParam(value = "data-url", required = false) String dataUrl,
                          Model model) {

        if (fontsize != null && isNumeric(fontsize)) {
            fontsize += "px";
        }

        List<Column> columns = toColumns(columnes);
        model.addAttribute("columns", columns);
        model.addAttribute("showTableHeader", "yes".equalsIgnoreCase(showtableheader));
        model.addAttribute("linkOriginalDoc", "yes".equalsIgnoreCase(linkoriginaldoc));
        model.addAttribute("hideRightColumn", "yes".equalsIgnoreCase(hiderightcolumn));
        model.addAttribute("fontSize", fontsize);
        model.addAttribute("defaultSortColumn", defaultsortcolumn);
        model.addAttribute("defaultSortOrder", defaultsortorder);

        IFeed filter = iFeedService.getFeedForSolrQuery(Long.valueOf(feedid));
        if (filter == null) {
            throw new ResourceNotFoundException();
        }

        if (defaultsortcolumn != null && "dc.title".equals(defaultsortcolumn)) {
            defaultsortcolumn = "title";
        }

        FieldInf fieldInf = iFeedService.getFieldInf(defaultsortcolumn);

        List<String> selectionPart = new ArrayList<>(columns.stream().map(c -> c.getKey()).collect(Collectors.toList()));
        selectionPart.add("id");
        selectionPart.add("dc.identifier.native");
        selectionPart.add("originalDownloadLatestVersionUrl");
        selectionPart.add("url");
        selectionPart.add("dc.date.validto");
        selectionPart.add("dc.date.validfrom");
        selectionPart.add("title");
        selectionPart.add(defaultsortcolumn);
        selectionPart.addAll(Arrays.asList(FieldInf.toIdsList(iFeedService.getFieldInfs(), selectionPart)));

        Result findings = client.query(
                filter.toQuery(client.fetchFields()),
                0,
                (limit.intValue() > 0 ? limit : 1_000_000),
                defaultsortorder, fieldInf,
                selectionPart.toArray(new String[selectionPart.size()])
        );

        fillInCounterpartValues(findings.getDocumentList().getDocuments(), iFeedService.getFieldInfs());

        model.addAttribute("items", findings.getDocumentList().getDocuments());

        for (Map<String, Object> item : findings.getDocumentList().getDocuments()) {
            item.put("dc.title", item.get("title"));
            String validToKey = "dc.date.validto";
            String textDate = (String) item.get(validToKey);
            String warning = "";
            if (!isBlanc(textDate) && isTimeStampPassed(textDate)) {
                warning = ("Dokumentet har gått ut: " + format(textDate));
            }

            String validFromKey = "dc.date.validfrom";
            textDate = (String) item.get(validFromKey);
            if (!isBlanc(textDate) && isTimeStampInFuture(textDate)) {
                warning += "\nDokumentet börjar gälla: " + format(textDate);
            }

            if (!isBlanc(warning)) {
                item.put("warning", warning.trim());
            }

            for (Column column : columns) {

                item.put(column.getKey(), format(item.get(column.getKey())));
                if (column.getKey().toLowerCase().contains("date")) {
                    String value = (String) item.get(column.getKey());
                    if (value != null && value.length() > 10) {
                        item.put(column.getKey(), value.substring(0, 10));
                    }

                }
            }

            String id = item.get("id").toString().replace("workspace://SpacesStore/", "");
            item.put("id", id);

            if ("yes".equals(linkoriginaldoc)) {
                item.put("url",
                        getFirstNotEmpty(
                                item.get("yes".equals(linkoriginaldoc) ? "dc.identifier.native" : "url"),
                                item.get("yes".equals(linkoriginaldoc) ? "originalDownloadLatestVersionUrl" : "url")
                        )
                );
            }
        }

        for (IFeedFilter iff : filter.getFilters()) {
            Metadata md = iff.getMetadata();
            if (md != null && md.getFilterQuery() != null) {
                for (Map<String, Object> item : findings.getDocumentList().getDocuments()) {

                }
            }
        }

        return "display";
    }

    static void fillInCounterpartValues(List<Map<String, Object>> fromIntoItems, List<FieldInf> basedOnThose) {
        for (Map<String, Object> fromIntoItem : fromIntoItems) {
            fillInCounterpartValues(fromIntoItem, basedOnThose);
        }
    }

    static void fillInCounterpartValues(Map<String, Object> fromInto, List<FieldInf> basedOnThose) {
        new FieldInf(basedOnThose).visit(item -> {
            final Object value = fromInto.get(item.getId());
            if (!isBlank(item.getId()) && !item.getCounterparts().isEmpty() && !isBlank(value)) {
                for (String counterpart : item.getCounterparts()) {
                    if (isBlank(fromInto.get(counterpart)))
                        fromInto.put(counterpart, value);
                }
            }
        });
    }

    static boolean isBlank(Object value) {
        if (value == null) return true;
        String asText = value.toString().trim();
        if (asText.equals("") || asText.equals("[]"))
            return true;
        return false;
    }

    private static Object getFirstNotEmpty(Object... items) {
        for (Object item : items) {
            if (item != null && !item.toString().trim().equals("")) {
                return item;
            }
        }
        return null;
    }

    public static boolean isTimeStampPassed(String textDate) {
        if (!DateFormatter.isSomeDate(textDate)) {
            return false;
        }
        Date date = DateFormatter.parse(textDate);
        Date now = new Date();
        return date.getTime() < now.getTime();
    }

    public static boolean isTimeStampInFuture(String textDate) {
        if (!DateFormatter.isSomeDate(textDate)) {
            return false;
        }
        Date date = DateFormatter.parse(textDate);
        Date now = new Date();
        return date.getTime() > now.getTime();
    }

    private boolean isBlanc(String textDate) {
        return textDate == null || "".equals(textDate.trim());
    }

    private static Object format(Object value) {
        if (value instanceof Collection) {
            return format((Collection) value);
        }
        if (DateFormatter.isSomeDate(value)) {
            if (value instanceof String) {
                return DateFormatter.formatTextToDateOnly((String) value);
            }
            return DateFormatter.format((Date) value);
        }
        return value;
    }

    static Object format(Collection value) {
        Collection collection = (Collection) value;
        List result = new ArrayList() {
            @Override
            public String toString() {
                return String.join(
                        ", ",
                        (List) stream().map(i -> String.valueOf(i)).collect(Collectors.toList())
                );
            }
        };
        for (Object v : collection) {
            result.add(format(v));
        }
        return result;
    }

    /**
     * Using path variables to asscess a feed from a http client.
     *
     * @param documentId thw id of the document (in alfresco).
     * @param model      data to be used in rendering the data.
     * @return what jsp to use.
     */
    @RequestMapping(value = "/documents/{documentId}/metadata")
    public String details(@PathVariable String documentId, Model model,
                          HttpServletResponse response,
                          @RequestParam(value = "type", required = false) String type) {
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
        // final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(fullId);
        final SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        IFeedFilter filter = new IFeedFilter();
        filter.setFilterQuery(documentId);
        filter.setFilterKey("id");
        Result findigs = client.query(filter.toQuery(client.fetchFields()), null, null, null, null);
        if (findigs.getDocumentList().getDocuments().isEmpty()) {
            filter.setFilterQuery("workspace://SpacesStore/" + documentId);
            findigs = client.query(filter.toQuery(client.fetchFields()), null, null, null, null);
        }

        if (findigs.getDocumentList().getDocuments().isEmpty()) {
            throw new ResourceNotFoundException();
        }

        final Map<String, Object> doc = findigs.getDocumentList().getDocuments().get(0);
        List<FieldInf> infs = iFeedService.getFieldInfs();
        FieldInf root = new FieldInf(infs);

        for (FieldInf inf : infs) {
            inf.visit(item -> {
                String qp = item.getQueryPrefix();
                if (qp != null && doc.containsKey(item.getId())) {
                    Object allValue = doc.get(item.getId());
                    System.out.println("allValue = " + allValue);
                    if (allValue instanceof Collection) {
                        Collection values = (Collection) allValue;
                        List<String> formatted = (List<String>) values.stream()
                                .filter(o -> o.toString().startsWith(qp))
                                .map(v -> {
                                    String[] parts = v.toString().split(Pattern.quote("/"));
                                    return parts[parts.length - 1];
                                })
                                .collect(Collectors.toList());
                        String key = item.getId() + " " + item.getQueryPrefix();
                        doc.put(key, formatted);
                        System.out.println(key + " = " + formatted);
                    }
                }
            });
        }

        for (String s : new HashSet<>(doc.keySet())) {
            Object value = ifHsaThenFormat(doc.get(s));
            if (value == null || value.toString().trim().equals("") || value.toString().trim().equals("[]")) {
                doc.remove(s);
                continue;
            }
            value = DateFormatter.formatTextDate(String.valueOf(value));
            if (value != null)
                if (value.toString().startsWith("[")) {
                    value = value.toString().replace("[", "").replace("]", "");
                }
            doc.put(s, value);
        }

        if ("2".equals(doc.get("vgr:VgrExtension.vgr:SecurityClass") + "")) {
            doc.put("vgr:VgrExtension.vgr:SecurityClass", "Inom regionen");
        } else if ("1".equals(doc.get("vgr:VgrExtension.vgr:SecurityClass") + "")) {
            doc.put("vgr:VgrExtension.vgr:SecurityClass", "Internet");
        }

        formatAnyHyperLinks(doc);

        response.setHeader("Access-Control-Allow-Origin", "*");

        // String sourceSystem = (String) doc.get("SourceSystem");
        model.addAttribute("item", doc);

        if ("tooltip".equals(type)) {
            System.out.print("tooltip -> ");
            if (isGoverning(doc)) {
                System.out.println("tooltipGovDocs");
                return "tooltipGovDocs";
            } else if (doc.containsKey("vgr:VgrExtension.vgr:SourceSystem.id")) {
                System.out.println("tooltipSofia");
                return "tooltipSofia";
            } else if (true) {
                System.out.println("tooltipAlfrescoBarium");
                return "tooltipAlfrescoBarium";
            }
            root.initForMiniView(doc);
        } else {
            root.initForMaxView(doc);
            // dc.type.document.structure = [Styrande dokument]
            if (isGoverning(doc)) {
                System.out.println("Is governing dock.");
                List<KeyLabel> pairs = new ArrayList<>();
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:Title", "Titel"));
                pairs.add(new KeyLabel("core:ArchivalObject.core:Description", "Beskrivning"));
                pairs.add(new KeyLabel("core:ArchivalObject.core:Producer", "Myndighet"));

                pairs.add(new KeyLabel("core:ArchivalObject.core:Classification.core:Classification.name", "Klassificeringsstruktur (process)"));

                ifHavingValueChangeThat(doc, "core:ArchivalObject.core:Classification.core:Classification.name",
                        doc.get("core:ArchivalObject.core:Classification.core:Classification.name"),
                        " (", doc.get("core:ArchivalObject.core:Classification.core:Classification.classCode"), ")");

                pairs.add(new KeyLabel("core:ArchivalObject.core:ObjectType", "Handlingstyp"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:PublishedForUnit.id", "Upprättat för enhet"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ValidityArea", "Giltighetsområde"));
                pairs.add(new KeyLabel("vgrsy:DomainExtension.vgrsy:SubjectClassification", "Regional ämnesindelning"));
                pairs.add(new KeyLabel("vgrsy:DomainExtension.vgrsy:SubjectLocalClassification", "Egen ämnesindelning"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:Tag", "Företagsnyckelord"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:Source.id", "DokumentId källa"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:SourceSystem", "Källsystem"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:CreatedByUnit.id", "Upprättat av enhet"));

                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:CreatedBy.id", "Upprättat av (person)"));
                ifHavingValueChangeThat(doc, "vgr:VgrExtension.vgr:CreatedBy.id",
                        doc.get("vgr:VgrExtension.vgr:CreatedBy"), " ",
                        "(" + doc.get("vgr:VgrExtension.vgr:CreatedBy.id") + ")");

                pairs.add(new KeyLabel("core:ArchivalObject.core:CreatedDateTime", "Upprättat datum"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ValidFrom", "Giltig från"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ValidTo", "Giltig till"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:AvailableFrom", "Tillgänglig f o m"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:AvailableTo", "Tillgänglig t o m"));
                pairs.add(new KeyLabel("vgr:VgrExtension.vgr:SecurityClass", "Åtkomsträtt"));
                // pairs.add(new KeyLabel("vgr:VgrExtension.vgr:SecurityClass", "Åtkomsträtt"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path SweMeSH/", "SweMesh")); // Specialare!
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path Verksamhetskod/", "Verksamhetskod enligt HSA"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ContentResponsible", "Innehållsansvarig"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ContentResponsible.role", "Innehållsansvarig, roll"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ContentReviewer", "Innehållsgranskare"));
                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:DocumentApproved.name", "Godkänt av"));

                ifHavingValueChangeThat(doc, "vgrsd:DomainExtension.vgrsd:DocumentApproved.name",
                        doc.get("vgrsd:DomainExtension.vgrsd:DocumentApproved.name"), " (",
                        doc.get("vgrsd:DomainExtension.vgrsd:DocumentApproved.id"), ")");

                // vgrsd:DomainExtension.vgrsd:ContentReviewer
                ifHavingValueChangeThat(doc, "vgrsd:DomainExtension.vgrsd:ContentReviewer",
                        doc.get("vgrsd:DomainExtension.vgrsd:ContentReviewer"), " (",
                        doc.get("vgrsd:DomainExtension.vgrsd:ContentReviewer.id"), ")");

                ifHavingValueChangeThat(doc, "vgrsd:DomainExtension.vgrsd:DocumentApproved.name",
                        doc.get("vgrsd:DomainExtension.vgrsd:DocumentApproved.name"), " (",
                        doc.get("vgrsd:DomainExtension.vgrsd:DocumentApproved"), ")");

                ifHavingValueChangeThat(doc, "vgrsd:DomainExtension.vgrsd:ContentResponsible",
                        doc.get("vgrsd:DomainExtension.vgrsd:ContentResponsible"), " (",
                        doc.get("vgrsd:DomainExtension.vgrsd:ContentResponsible.id"), ")");

                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:DocumentApproved.role", "Godkänt av, roll"));
                pairs.add(new KeyLabel("version", "Version"));

                pairs.add(new KeyLabel("url", "Länk för webben"));
                pairs.add(new KeyLabel("originalDownloadLatestVersionUrl", "Länk för webben (orginalformat)"));

                pairs.add(new KeyLabel("vgrsy:DomainExtension.vgrsy:SiteInfo.name", "Ursprunglig samarbetsyta"));


                model.addAttribute("meta", pairs);

                return "documentDetailsGovDocs";
            } else if (doc.containsKey("vgr:VgrExtension.vgr:SourceSystem.id")) {
                // SOFIA thing.
                return "documentDetailsSofia";
            } else {
                // Alfresco/Barium
                return "documentDetailsAlfrescoBarium";
            }
        }

        if (root.getChildren().isEmpty()) {
            FieldInf reserve = new FieldInf();
            reserve.setName("Dokumentinfo (templösning då config saknas)");
            FieldInf title = new FieldInf();
            title.setInTooltip("tooltip".equals(type));
            title.setId("title");
            title.setName("Titel");
            reserve.getChildren().add(title);
            reserve.setInTooltip("tooltip".equals(type));
            FieldInf first = new FieldInf(Arrays.asList(reserve));
            first.setInTooltip("tooltip".equals(type));
            root.getChildren().add(first);
        }

        FieldInf result = root.getChildren().get(0);

        for (FieldInf child : root.getChildren()) {
            if (result.childCount() < child.childCount()) {
                result = child;
            }
        }

        result.setValue((String) doc.get("title"));
        model.addAttribute("doc", result);


        if ("tooltip".equals(type)) {
            return "documentDetailsTooltip";
        } else {
            return "documentDetails";
        }
        // return "documentDetails";
    }

    private void swapInApelonValues(List<KeyLabel> keyLabels) {
        List<FieldInf> fields = iFeedService.getFieldInfs();

    }

    private static void ifHavingValueChangeThat(Map<String, Object> valueInHere, String withKey, Object... toThese) {
        if (!valueInHere.containsKey(withKey)) {
            return;
        }
        Object oldValue = valueInHere.get(withKey);
        if (oldValue == null || oldValue.toString().trim().equals("") || oldValue.toString().trim().equals("[]")) {
            return;
        }
        String nv = String.join("", Arrays.stream(toThese).map(thing -> thing + "").collect(Collectors.toList()));
        valueInHere.put(withKey, nv);
    }

    public static class KeyLabel {
        private String key, label;

        public KeyLabel(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

    }

    void completeVgrId(Map<String, Object> doc) {
        List<FieldInf> fields = iFeedService.getFieldInfs();
        Map<String, FieldInf> mappedFields = new HashMap<>();
        fields.forEach(f -> f.visit(item -> mappedFields.put(item.getId(), item)));
        for (String key : doc.keySet()) {
            String value = (String) doc.get(key);
            if (value != null && !"".equals(value.trim()) && mappedFields.containsKey(key)) {
                FieldInf field = mappedFields.get(key);
                if (field != null) {
                    if ("d:ldap_value".equals(field.getType())) {
                        System.out.println("Hittade d:ldap_value");
                        List<Person> result = ldapPersonService.getPeople(value, 1);
                        if (result.size() == 1) {
                            System.out.println("Hittade för " + key + " " + value);
                            doc.put(key, result.get(0).getNiceName());
                            System.out.println(" " + doc.get(key));
                        }
                    }
                }
            }
        }
    }

    private static boolean isGoverning(Map<String, Object> doc) {
        return "Styrande dokument".equals(doc.get("vgrsd:DomainExtension.domain"));
    }

    private FieldInf getApropriateFieldInf(String type, Map<String, Object> doc) {
        FieldInf root = new FieldInf(iFeedService.getFieldInfs());

        for (String s : doc.keySet()) {
            doc.put(s, ifHsaThenFormat(doc.get(s)));
        }

        formatAnyHyperLinks(doc);

        if ("tooltip".equals(type)) {
            root.initForMiniView(doc);
        } else {
            root.initForMaxView(doc);
        }


        FieldInf result = root.getChildren().get(0);
        return result;
    }

    private void formatAnyHyperLinks(Map<String, Object> doc) {
        for (String key : doc.keySet()) {
            Object value = doc.get(key);
            if (value instanceof String) {
                String text = (String) value;
                if (text.startsWith("https://") || text.startsWith("http://")) {
                    doc.put(key, String.format("<a href=\"%s\">%s</a>", text, text));
                }
            }
        }
    }

    /*private List<LabelledValue> newAlfrescoBariumDisplayFieldsWithoutValue() {
        List<LabelledValue> result = new ArrayList<>();
        result.add(new LabelledValue("", "Dokumentbeskrivning"));
        result.add(new LabelledValue("dc.title", "Titel (autokomplettering)"));
        result.add(new LabelledValue("dc.title.filename", "Filnamn, utgivet/publicerat"));
        result.add(new LabelledValue("dc.title.filename.native", "Filnamn, original"));
        result.add(new LabelledValue("dc.title.alternative", "Alternativ titel"));
        result.add(new LabelledValue("dc.description", "Beskrivning"));
        result.add(new LabelledValue("dc.type.document", "Gruppering av handlingstyper"));
        result.add(new LabelledValue("dc.type.document.structure", "Dokumentstruktur VGR"));
        result.add(new LabelledValue("dc.type.document.structure.id", "Dokumentstruktur VGR ID"));
        result.add(new LabelledValue("dc.type.record", "Handlingstyp (autokomplettering)"));
        result.add(new LabelledValue("dc.coverage.hsacode", "Verksamhetskod enligt HSA"));
        result.add(new LabelledValue("dcterms.audience", "Målgrupp HoS (autokomplettering)"));
        result.add(new LabelledValue("dc.audience", "Målgrupp HoS (autokomplettering)"));
        result.add(new LabelledValue("dc.identifier.version", "Version"));
        result.add(new LabelledValue("dc.contributor.savedby", "Sparat av"));
        result.add(new LabelledValue("dc.contributor.savedby.id", "Sparat av ID"));
        result.add(new LabelledValue("dc.date.saved", "Sparat datum"));
        result.add(new LabelledValue("vgregion.status.document", "Dokumentstatus"));
        result.add(new LabelledValue("vgr.status.document", "Dokumentstatus"));
        result.add(new LabelledValue("vgr.status.document.id", "Dokumentstatus"));
        result.add(new LabelledValue("dc.source.documentid", "Dokumentid källa"));
        result.add(new LabelledValue("dc.source", "Länk till dokumentets källa"));
        result.add(new LabelledValue("", "Skapat av och för"));
        result.add(new LabelledValue("dc.creator", "Skapat av"));
        result.add(new LabelledValue("dc.creator.id", "Skapat av ID"));
        result.add(new LabelledValue("dc.creator.freetext", "Skapat av (Fritext)"));
        result.add(new LabelledValue("dc.creator.forunit", "Skapat av enhet (autokomplettering)"));
        result.add(new LabelledValue("dc.creator.forunit.id", "Skapat av enhet ID (VGR:s organisationsträd)"));
        result.add(new LabelledValue("dc.creator.project-assignment", "Skapat av Projekt/Uppdrag/Grupp"));
        result.add(new LabelledValue("", "Ansvariga"));
        result.add(new LabelledValue("dc.creator.document", "Innehållsansvarig/Dokumentansvarig"));
        result.add(new LabelledValue("dc.creator.document.id", "Innehållsansvarig/Dokumentansvarig ID"));
        result.add(new LabelledValue("dc.creator.function", "Funktionsansvar"));
        result.add(new LabelledValue("dc.creator.recordscreator", "Arkivbildare (autokomplettering)"));
        result.add(new LabelledValue("dc.creator.recordscreator.id", "Arkivbildare ID (VGR:s organisationsträd)"));
        result.add(new LabelledValue("", "Giltighet och tillgänglighet"));
        result.add(new LabelledValue("dc.date.validfrom", "Giltighetsdatum from"));
        result.add(new LabelledValue("dc.date.validto", "Giltighetsdatum tom"));
        result.add(new LabelledValue("dc.date.availablefrom", "Tillgänglighetsdatum from"));
        result.add(new LabelledValue("dc.date.availableto", "Tillgänglighetsdatum tom"));
        result.add(new LabelledValue("dc.date.copyrighted", "Copyrightdatum"));
        result.add(new LabelledValue("", "Granskat/Godkänt"));
        result.add(new LabelledValue("dc.contributor.acceptedby", "Godkänt av"));
        result.add(new LabelledValue("dc.contributor.acceptedby.id", "Godkänt av ID"));
        result.add(new LabelledValue("dc.contributor.acceptedby.freetext", "Godkänt av (Fritext)"));
        result.add(new LabelledValue("dc.date.accepted", "Godkänt datum"));
        result.add(new LabelledValue("dc.contributor.acceptedby.role", "Godkänt av Egenskap/Roll"));
        result.add(new LabelledValue("dc.contributor.acceptedby.unit.freetext", "Enhet (Fritext)"));
        result.add(new LabelledValue("dc.contributor.controlledby", "Granskat av"));
        result.add(new LabelledValue("dc.contributor.controlledby.id", "Granskat av ID"));
        result.add(new LabelledValue("dc.contributor.controlledby.freetext", "Granskat av (Fritext)"));
        result.add(new LabelledValue("dc.date.controlled", "Granskningsdatum"));
        result.add(new LabelledValue("dc.contributor.controlledby.role", "Granskat av Egenskap/Roll"));
        result.add(new LabelledValue("dc.contributor.controlledby.unit.freetext", "Enhet (Fritext)"));
        result.add(new LabelledValue("", "Publicerat"));
        result.add(new LabelledValue("dc.publisher.forunit", "Publicerat för enhet (autokomplettering)"));
        result.add(new LabelledValue("dc.publisher.forunit.flat", "Publicerat för enhet (för sortering)"));
        result.add(new LabelledValue("dc.publisher.forunit.id", "Publicerat för enhet ID (VGR:s organisationsträd)"));
        result.add(new LabelledValue("dc.publisher.project-assignment", "Publicerat för Projekt/Uppdrag/Grupp"));
        result.add(new LabelledValue("dc.rights.accessrights", "Publik åtkomsträtt"));
        result.add(new LabelledValue("dc.publisher", "Publicerat av"));
        result.add(new LabelledValue("dc.publisher.id", "Publicerat av ID"));
        result.add(new LabelledValue("dc.date.issued", "Publiceringsdatum"));
        result.add(new LabelledValue("dc.identifier", "Länk till publicerat/utgivet dokument"));
        result.add(new LabelledValue("dc.identifier.native", "Länk till utgivet originaldokument"));
        result.add(new LabelledValue("", "Sammanhang"));
        result.add(new LabelledValue("dc.type.process.name", "Processnamn"));
        result.add(new LabelledValue("dc.type.file.process", "Ärendetyp"));
        result.add(new LabelledValue("dc.type.file", "Ärende"));
        result.add(new LabelledValue("dc.identifier.diarie.id", "Diarienummer"));
        result.add(new LabelledValue("dc.type.document.serie", "Dokumentserie"));
        result.add(new LabelledValue("dc.type.document.id", "Referensnummer i dokumentserie"));
        result.add(new LabelledValue("", "Nyckelord"));
        result.add(new LabelledValue("dc.subject.keywords", "Nyckelord (autokomplettering)"));
        result.add(new LabelledValue("dc.subject.authorkeywords", "Författarens nyckelord"));
        result.add(new LabelledValue("", "Övrigt"));
        result.add(new LabelledValue("language", "Språk"));
        result.add(new LabelledValue("dc.relation.isversionof", "Alternativ variant av"));
        result.add(new LabelledValue("dc.relation.replaces", "Ersätter"));
        result.add(new LabelledValue("dc.format.extent", "Omfattning"));
        result.add(new LabelledValue("dc.identifier.location", "Fysisk placering"));
        result.add(new LabelledValue("dc.type.templatename", "Mallnamn"));
        result.add(new LabelledValue("dc.ifHsaThenFormat.extent.mimetype", "Mimetyp, utgivet/publicerat"));
        result.add(new LabelledValue("dc.ifHsaThenFormat.extent.mimetype.native", "Mimetyp, original"));
        result.add(new LabelledValue("dc.ifHsaThenFormat.extension", "Filändelse, utgivet/publicerat"));
        result.add(new LabelledValue("dc.ifHsaThenFormat.extension.native", "Filändelse, original"));
        result.add(new LabelledValue("dc.identifier.checksum", "Kontrollsumma dokument, utgivet/publicerat"));
        result.add(new LabelledValue("dc.identifier.checksum.native", "Kontrollsumma dokument, original"));
        result.add(new LabelledValue("dc.source.origin", "Källsystem"));
        return result;
    }*/

    private List<LabelledValue> newSofiaDisplayFieldsWithoutValue() {
        List<LabelledValue> result = new ArrayList<>();

        result.add(new LabelledValue("vgrsd:DomainExtension.vgrsd:ContentResponsible", "Innehållsansvarig"));
        result.add(new LabelledValue("vgrsd:DomainExtension.vgrsd:DocumentApprover", "Godkänt av"));
        result.add(new LabelledValue("vgrsd:DomainExtension.vgrsd:ContentReviewer", "Granskad av"));

        result.add(new LabelledValue("core:ArchivalObject.core:CreatedDateTime", "Upprättad datum"));
        result.add(new LabelledValue("core:ArchivalObject.core:PreservationPlanning.action", "Bevarande och gallringsåtgärd"));
        result.add(new LabelledValue("core:ArchivalObject.core:PreservationPlanning.RDA", "Bevarande och gallringsbeslut"));
        result.add(new LabelledValue("revisiondate", "Gallringsdatum"));
        result.add(new LabelledValue("core:ArchivalObject.core:AccessRight", "Åtkomsträtt i slutarkiv"));
        result.add(new LabelledValue("core:ArchivalObject.core:Description", "Dokumentbeskrivning i Sharepoint, Beskrivning i Mellanarkivet"));
        result.add(new LabelledValue("core:ArchivalObject.core:ObjectType", "Handlingstyp"));
        result.add(new LabelledValue("core:ArchivalObject.core:ObjectType.id", ""));
        result.add(new LabelledValue("core:ArchivalObject.core:ObjectType.filePlan", "Dokumenthanteringsplan"));
        result.add(new LabelledValue("core:ArchivalObject.core:Classification.core:Classification.id", "Id på klassificering"));
        result.add(new LabelledValue("core:ArchivalObject.core:Classification.core:Classification.classCode", "Punktnotation på klassificering"));
        result.add(new LabelledValue("core:ArchivalObject.core:Classification.core:Classification.level", "Nivå på klassificering"));
        result.add(new LabelledValue("core:ArchivalObject.core:Classification.core:Classification.name", "Namn på klassificering"));
        result.add(new LabelledValue("core:ArchivalObject.core:Unit", "Rubrik"));
        result.add(new LabelledValue("core:ArchivalObject.core:Unit.refcode", "Signum"));
        result.add(new LabelledValue("core:ArchivalObject.core:Unit.level", "Nivå i arkivförteckningen"));
        result.add(new LabelledValue("core:ArchivalObject.core:Producer", "Myndighet/Arkivbildare"));
        result.add(new LabelledValue("core:ArchivalObject.core:Producer.idType", ""));
        result.add(new LabelledValue("core:ArchivalObject.core:Producer.id", "Myndighetens HSA-ID"));
        result.add(new LabelledValue("vgr:VgrExtension.itemId", "Arkivobjekt-ID"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:SourceSystem", "Källsystem"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:SourceSystem.id", "Källsystem-ID"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:Source.id", "Käll-ID"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:Source.version", "Version i källsystem"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:Source.versionId", "N/A"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:Title", "Rubrik i Sharepoint, Titel i Mellanarkivet"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:AvailableFrom", "Tillgänglig från"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:AvailableTo", "Tillgänglig till"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:RevisedAvailableFrom", "Reviderat tillgänglig från"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:RevisedAvailableTo", "Reviderat tillgänglig till"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:SecurityClass", "Åtkomsträtt"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:RestrictionCode", "Skyddskod"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:LegalParagraph", "Lagparagraf"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:CreatedByUnit", "Upprättad av enhet"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:CreatedByUnit.id", ""));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:PublishedForUnit", "Upprättad för enhet"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:PublishedForUnit.id", ""));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:CreatedBy", "Upprättad av"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:CreatedBy.id", "Upprättad av (vgrid)"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:CreatedBy.org", "Upprättad av (org)"));
        result.add(new LabelledValue("vgr:VgrExtension.vgr:Tag", "Företagsnyckelord i Sharepoint, Nyckelord i Mellanarkivet"));
        result.add(new LabelledValue("vgrsy:DomainExtension.itemId", ""));
        result.add(new LabelledValue("vgrsy:DomainExtension.domain", "Domännamn"));
        result.add(new LabelledValue("vgrsy:DomainExtension.vgrsy:SubjectClassification", "Regional ämnesindelning"));
        result.add(new LabelledValue("vgrsy:DomainExtension.vgrsy:SubjectLocalClassification", "Egen ämnesindelning"));
        result.add(new LabelledValue("vgrsy:DomainExtension.domain", ""));
        result.add(new LabelledValue("core:ArchivalObject.core:CreatedDateTime", "Skapad datum"));
        result.add(new LabelledValue("core:ArchivalObject.core:PreservationPlanning.action", "Bevarande och gallringsåtgärd"));
        result.add(new LabelledValue("core:ArchivalObject.core:PreservationPlanning.RDA", "Bevarande och gallringsbeslut"));
        result.add(new LabelledValue("revisiondate", "Gallringsdatum"));
        result.add(new LabelledValue("core:ArchivalObject.core:AccessRight", "Åtkomsträtt i slutarkiv"));
        result.add(new LabelledValue("core:ArchivalObject.core:Description", ""));
        result.add(new LabelledValue("core:ArchivalObject.core:CreatedDateTime", "Skapad datum"));

        result.add(new LabelledValue("productionDownloadLatestVersionUrl", "Webblänk produktionsformat"));
        result.add(new LabelledValue("originalDownloadLatestVersionUrl", "Webblänk urspurungsformat"));
        result.add(new LabelledValue("archivalDownloadLatestVersionUrl", "Webblänk arkivformat"));

        // pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ValidFrom", "Giltig från"));
        //                pairs.add(new KeyLabel("vgrsd:DomainExtension.vgrsd:ValidTo", "Giltig till"));

        result.add(new LabelledValue("vgrsd:DomainExtension.vgrsd:ValidFrom", "Giltig från"));
        result.add(new LabelledValue("vgrsd:DomainExtension.vgrsd:ValidTo", "Giltig till"));

        return result;
    }


    public static class LabelledValue {

        private String key;
        private String label;
        private Object value;

        public LabelledValue(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }


    // @RequestMapping(value = "/documents/{documentId}/metadata")
    public String detailsOld(@PathVariable String documentId, Model model) {
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
        final SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        IFeedFilter filter = new IFeedFilter();
        filter.setFilterQuery(documentId);
        filter.setFilterKey("id");
        // Map<String, Object> doc = client.query(filter.toQuery(), 0, 1, "").getResponse().getDocs().get(0);

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

    @Autowired
    ObjectRepo objectRepo;

    private String webScriptJsonUrl = getProperties().getProperty("ifeed.web.script.json.url");

    private String webScriptUrl = getProperties().getProperty("ifeed.web.script.url");

    @RequestMapping(value = "/table-view/{id}")
    @Transactional
    public String tableView(@PathVariable(name = "id") Long id, Model model) {
        DynamicTableDef dtd = objectRepo.findByPrimaryKey(DynamicTableDef.class, id);

        if (dtd != null) {
            model.addAttribute("tableName", dtd.getName());
            model.addAttribute("iFeedCode", toTableMarkup(dtd, false));
            model.addAttribute("webScriptJsonUrl", webScriptJsonUrl);
            model.addAttribute("webScriptUrl", webScriptUrl);
        }
        return "table-view";
    }

    private static Properties properties;

    public static Properties getProperties() {
        if (properties != null)
            return properties;
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties");
            Properties result = new Properties();
            result.load(new FileInputStream(path.toFile()));
            return properties = result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
