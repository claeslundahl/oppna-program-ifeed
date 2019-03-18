package se.vgregion.ifeed.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.ifeed.service.alfresco.store.AlfrescoDocumentService;
import se.vgregion.ifeed.service.alfresco.store.DocumentInfo;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ldap.LdapSupportService;
import se.vgregion.ldap.person.LdapPersonService;

import java.util.List;

//import org.springframework.context.MessageSource;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({"ifeed"})
public class ShowDocumentMetadataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowDocumentMetadataController.class);

    private AlfrescoDocumentService alfrescoMetadataService;

    private List<String> topListMetadata;

    @Autowired
    private LdapPersonService ldapPersonService;

    /*@Autowired
    private MessageSource resource;*/

    @Autowired
    private LdapSupportService ldapOrganizationService;

    public ShowDocumentMetadataController() {

    }

    public ShowDocumentMetadataController(AlfrescoDocumentService documentService) {
        this.alfrescoMetadataService = documentService;
    }

    public void setTopListMetadata(List<String> topListMetadata) {
        this.topListMetadata = topListMetadata;
    }

    @RenderMapping(params = "view=documentMetadata")
    public String showDocumentMetadata(@ModelAttribute("ifeed") final IFeed iFeed, Model model, @RequestParam(required = true) final String documentId) {
        final DocumentInfo documentInfo = alfrescoMetadataService.getDocumentInfo(documentId);
        model.addAttribute("documentInfo", documentInfo);
        model.addAttribute("ifeed", iFeed);
        return "documentMetadata";
    }

    /*@ResourceMapping
    public void showTopDocumentMetadata(@RequestParam(required = true) final String documentId,
                                        ResourceResponse response) {
        Map<String, String> metadataPair = getMetdataValues(documentId);
        sendJsonResponse(response, metadataPair);
    }

    protected Map<String, String> getMetdataValues(final String documentId) {
        Map<String, String> metadataPair = new HashMap<String, String>(topListMetadata.size());
        DocumentInfo documentInfo = null;

        try {
            documentInfo = alfrescoMetadataService.getDocumentInfo(documentId);
            LOGGER.debug("Document info retrieved from alfresco: {}", documentInfo);

            String metaValue = "";
            for (String metadata : topListMetadata) {
                Object metaValueObj = documentInfo.getMetadataValue(metadata, Object.class);
                metaValue = metaValueObj.toString();
                if (StringUtils.isBlank(metaValue)) {
                    LOGGER.warn("Metadata, {}, not found in document with id: {}", metadata, documentId);
                }
                // metadataPair.put(resource.getMessage(metadata + ".label", null, metadata, null), metaValue);
            }
        } catch (IFeedServiceException e) {
            metadataPair.put("Varning", "Ingen metadata kunde hittas!");
        }
        return metadataPair;
    }

    void sendJsonResponse(ResourceResponse response, Map<String, String> metadataPair) {
        try {
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            new ObjectMapper().writeValue(out, metadataPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   *//* MessageSource getResource() {
        return resource;
    }

    void setResource(MessageSource resource) {
        this.resource = resource;
    }*//*

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAlfrescoDocumentServiceException(Exception e) {
        if (!(e instanceof IFeedServiceException)) {
            LOGGER.error("IFeed Excption: {}", e);
            e = new IFeedServiceException("error.unhandled", "Internal IFeed Error", e);
        }
        return new ModelAndView("ExceptionHandler", Collections.singletonMap("exception", e));
    }

    @ResourceMapping("findPeople")
    public void searchPeople(@RequestParam final String filterValue, ResourceResponse response) {
        List<Person> people = ldapPersonService.getPeople(filterValue, 10);
        try {
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            new ObjectMapper().writeValue(out, people);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResourceMapping("findOrganizationByHsaId")
    public void findOrganizationByHsaId(@RequestParam String hsaId,
                                        @RequestParam(required = false) String callback, @RequestParam(required = false) Integer maxHits,
                                        ResourceResponse response) {
        try {
            hsaId = (EditIFeedController.replace(hsaId, EditIFeedController.charDecodeing));
            String json = IdentityUtil.vgrHsaIdToJson(hsaId, maxHits);
            if ("[]".equals(json)) {
                json = IdentityUtil.dnToJson(hsaId, maxHits);
            }
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            if (callback != null) {
                out.write(callback.getBytes());
                out.write("(".getBytes());
                out.write(json.getBytes());
                out.write(")".getBytes());
                out.flush();
            } else {
                out.write(json.getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResourceMapping("findOrgs")
    public void searchOrg(@RequestParam String parentOrg, @RequestParam String url, ResourceResponse response)
            throws IOException {
        parentOrg = EditIFeedController.replace(parentOrg, EditIFeedController.charDecodeing);
        parentOrg = URLDecoder.decode(parentOrg, "UTF-8");
        url = URLDecoder.decode(url, "UTF-8");

        VgrOrganization org = new VgrOrganization();
        org.setDn(parentOrg);

        String result = getVgrOrganizationJsonPart(org, url);
        final OutputStream out = response.getPortletOutputStream();
        out.write(result.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private String getVgrOrganizationJsonPart(VgrOrganization org, String url2portlet)
            throws JsonGenerationException, JsonMappingException, IOException {

        List<VgrOrganization> orgs = getLdapOrganizationService().findChildNodes(org);

        addDataTo(orgs, url2portlet, "io");

        return toJson(orgs);

    }

    public void addDataTo(List<VgrOrganization> vos, String ns, String type) throws UnsupportedEncodingException {
        for (VgrOrganization vo : vos) {
            addDataTo(vo, ns, type);
        }
    }

    public void addDataTo(VgrOrganization vo, String portletUrl, String type) throws UnsupportedEncodingException {
        vo.setIo(portletUrl + "&parentOrg=" + URLEncoder.encode(vo.getDn(), "UTF-8") + "&url="
                + URLEncoder.encode(portletUrl, "UTF-8"));
        vo.setType("io");
        vo.setLeaf(ldapOrganizationService.findChildNodes(vo).isEmpty());
        vo.setType(type);
        vo.setDn(EditIFeedController.replace(vo.getDn(), EditIFeedController.charEncodeing));
    }

    static String toJson(Object o) {
        return Json.toJson(o);
    }


    @ResourceMapping(value = "updateJsonpEmbedCode")
    public void updateJsonpEmbedCode(ResourceRequest request, ResourceResponse response) throws IOException {
        Map<String, String[]> map = request.getParameterMap();

        for (String key : map.keySet()) {
            String[] arr = map.get(key);
            String str = key + " = " + Arrays.asList(arr);
        }

        String[] fields = map.get("field");
        String[] aliases = map.get("alias");
        String[] orientations = map.get("orientation");
        String[] widths = map.get("width");
        String ifeedId = map.get("ifeedId")[0];
        String limitList = map.get("limitList")[0];
        String sortColumn = map.get("sortColumn")[0];
        String sortOrder = map.get("sortOrder")[0];
        String hideEpiRightColumn = map.get("hideEpiRightColumn")[0];
        String fontSize = map.get("fontSize")[0];
        String showTableHeader = map.get("showTableHeader")[0];
        String linkOriginalDoc = map.get("linkOriginalDoc")[0];

        String result = toTableMarkup(ifeedId, limitList, sortColumn, sortOrder, hideEpiRightColumn, fontSize, showTableHeader, linkOriginalDoc, fields, aliases, orientations, widths);
        final OutputStream out = response.getPortletOutputStream();
        out.write(result.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private String toTableMarkup(String ifeedId, String limitList, String sortColumn, String sortOrder, String hideEpiRightColumn, String fontSize, String showTableHeader, String linkOriginalDoc, String[] fields, String[] aliases, String[] orientations, String[] widths) {
        StringBuilder sb = new StringBuilder();
        List<String> concat = new ArrayList<String>();
        for (int i = 0; i < fields.length; i++) {
            concat.add(join("|", fields[i], aliases[i], orientations[i], widths[i]));
        }
        String columns = join(concat, ",");


        String result = "<div \n" +
                "\tclass=\"ifeedDocList\" \n" +
                //"\tcolumnes=\"title|Titel|left,dc.date.issued|Publiceringsdatum|right\" \n" +
                "\tcolumnes=\"" + columns + "\" \n" +
                "\tfontSize=\"" + fontSize + "\" \n" +
                "\tdefaultsortcolumn=\"" + sortColumn + "\" \n" +
                "\tdefaultsortorder=\"" + sortOrder + "\" \n" +
                "\tshowTableHeader=\"" + showTableHeader + "\" \n" +
                "\tlinkOriginalDoc=\"" + linkOriginalDoc + "\" \n" +
                "\tlimit=\"" + limitList + "\" \n" +
                "\thiderightcolumn=\"" + hideEpiRightColumn + "\" \n" +
                "\tfeedid=\"" + ifeedId + "\">\n" +
                "</div><noscript><iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/" + ifeedId + "/?by=" + sortColumn + "&dir=" + sortOrder + "' id='iframenoscript' name='iframenoscript' style='width: 100%; height: 400px' frameborder='0'>\n" +
                "</iframe>\n" +
                "</noscript>";

        return result;
    }

    *//**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param items   the different strings to be concatenated
     * @return as string product of the parameters.
     *//*
    public static String join(String junctor, String... items) {
        return join(Arrays.asList(items), junctor);
    }

    *//**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param list    the different strings to be concatenated
     * @return as string product of the parameters.
     *//*
    public static String join(List<?> list, String junctor) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0) + "";
        }

        for (int i = 0, j = list.size() - 1; i < j; i++) {
            sb.append(list.get(i));
            sb.append(junctor);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }

    public void setLdapPersonService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    public LdapPersonService getLdapPersonService() {
        return ldapPersonService;
    }

    public LdapSupportService getLdapOrganizationService() {
        return ldapOrganizationService;
    }

    public void setLdapOrganizationService(LdapSupportService ldapOrganizationService) {
        this.ldapOrganizationService = ldapOrganizationService;
    }*/
}