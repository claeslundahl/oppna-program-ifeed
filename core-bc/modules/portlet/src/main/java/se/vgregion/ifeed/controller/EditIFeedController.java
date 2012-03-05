package se.vgregion.ifeed.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.util.UriTemplate;

import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.formbean.FilterFormBean;
import se.vgregion.ifeed.formbean.SearchResultList;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ldap.LdapSupportService;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "ifeed", "hits", "vgrOrganizationJson", "fields" })
public class EditIFeedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditIFeedController.class);

    private static List<FieldInf> fieldInfs;

    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private IFeedSolrQuery iFeedSolrQuery;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private LdapPersonService ldapPersonService;
    @Resource(name = "iFeedAtomFeed")
    private UriTemplate iFeedAtomFeed;
    @Resource(name = "iFeedWebFeed")
    private UriTemplate iFeedWebFeed;

    @Autowired
    private LdapSupportService ldapOrganizationService;

    private WeakReference<String> vgrOrganizationJsonCache = new WeakReference<String>(null);

    @ActionMapping(params = "action=editIFeed")
    public void editIFeed(@RequestParam(required = true) final Long feedId, final Model model,
            final ActionResponse response) {
        IFeed iFeed = iFeedService.getIFeed(feedId);
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @RenderMapping(params = "view=showEditIFeedForm")
    public String showEditIFeedForm(@ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam(defaultValue = "") final String orderByCol,
            @RequestParam(defaultValue = "") final String orderByType, final Model model, RenderResponse repsonse,
            PortletResponse pr) throws JsonGenerationException, JsonMappingException, IOException {
        // model.asMap().get("ifeed");

        // Priority of sort field is:
        // 1. Request parameter - orderByCol/orderByType.
        // 2. Stored value in iFeed.
        // 3. A default value set by the application.

        String sortField = IFeedSolrQuery.DEFAULT_SORT_FIELD;
        sortField = StringUtils.defaultIfEmpty(iFeed.getSortField(), sortField);
        sortField = StringUtils.defaultIfEmpty(orderByCol, sortField);

        String sortDirection = IFeedSolrQuery.DEFAULT_SORT_DIRECTION.name();
        sortDirection = StringUtils.defaultIfEmpty(iFeed.getSortDirection(), sortDirection);
        sortDirection = StringUtils.defaultIfEmpty(orderByType, sortDirection);

        iFeed.setSortField(sortField);
        iFeed.setSortDirection(sortDirection);

        List<Map<String, Object>> result = iFeedSolrQuery.getIFeedResults(iFeed);
        if (result.size() == iFeedSolrQuery.getRows()) {
            result.remove(result.size() - 1);
            model.addAttribute("hitsOverflow", true);
        } else {
            model.addAttribute("hitsOverflow", false);
        }
        model.addAttribute("hits", new SearchResultList(result));
        model.addAttribute("maxHits", iFeedSolrQuery.getRows() - 1);
        model.addAttribute("vgrOrganizationJson", getVgrOrganizationJson(pr.getNamespace()));
        model.addAttribute("fields", getFieldInfs());
        model.addAttribute("fieldsMap", mapFieldInfToId());

        model.addAttribute("atomFeedLink",
                iFeedAtomFeed.expand(iFeed.getId(), iFeed.getSortField(), iFeed.getSortDirection()));

        model.addAttribute("webFeedLink",
                iFeedWebFeed.expand(iFeed.getId(), iFeed.getSortField(), iFeed.getSortDirection()));

        final PortletURL portletUrl = repsonse.createRenderURL();
        portletUrl.setParameter("view", "showEditIFeedForm");
        model.addAttribute("portletUrl", portletUrl);
        model.addAttribute("orderByCol", iFeed.getSortField());
        model.addAttribute("orderByType", iFeed.getSortDirection());
        return "editIFeedForm";
    }

    protected User fetchUser(PortletRequest request) throws PortalException, SystemException {
        return PortalUtil.getUser(request);
    }

    @ActionMapping(params = "action=saveIFeed")
    public void editIFeed(@ModelAttribute("ifeed") final IFeed iFeed, final ActionResponse response,
            final Model model, PortletRequest request) throws SystemException, PortalException {
        System.out.println("\n\neditIFeed\n");
        User user = fetchUser(request);
        if (!AccessGuard.mayEditFeed(user, iFeed)) {
            throw new RuntimeException();
        }
        System.out.println("\n\neditIFeed 1\n");
        IFeed ifeed = iFeedService.updateIFeed(iFeed);
        System.out.println("\n\neditIFeed 2\n");
        model.addAttribute("ifeed", ifeed);
        System.out.println("\n\neditIFeed 3\n");
        model.addAttribute("saveAction", Boolean.TRUE);
        System.out.println("\n\neditIFeed 4\n");
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=updateIFeed")
    public void updateIFeed(@ModelAttribute("ifeed") final IFeed iFeed, final ActionResponse response,
            final Model model) {
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    protected Map<String, FieldInf> mapFieldInfToId() {
        return iFeedService.mapFieldInfToId();
    }

    @ActionMapping(params = "action=selectFilter")
    public void selectNewFilter(@ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam(required = false, value = "filter") String filter
            // @RequestParam(required = false, value = "filter") final Filter filter
            , final Model model, final ActionResponse response) throws IOException {

        FieldInf newFilter = mapFieldInfToId().get(filter);
        model.addAttribute("newFilter", newFilter);

        Collection<String> vocabulary = metadataService.getVocabulary(newFilter.getApelonKey());

        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson = new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);
        model.addAttribute("filterFormBean", new FilterFormBean());
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=addFilter")
    public void addFilter(@ModelAttribute("ifeed") final IFeed iFeed,
            @ModelAttribute final FilterFormBean filterFormBean, final ActionResponse response, final Model model) {

        LOGGER.debug("FilterFormBean: {}", ToStringBuilder.reflectionToString(filterFormBean));

        System.out.println("\n\nFilterFormBean: " + ToStringBuilder.reflectionToString(filterFormBean) + "\n\n");

        // if (filterFormBean.getFilterValue() == null && filterFormBean.getValidFromYear() != 0) {
        // Date date = new Date();
        // date.setYear(filterFormBean.getValidFromYear());
        // date.setMonth(filterFormBean.getValidFromMonth());
        // date.setDate(filterFormBean.getValidFromDay());
        //
        // filterFormBean.setFilterValue(date);
        // System.out.println("Datumet blev: " + date);
        // }

        iFeed.addFilter(new IFeedFilter(null, filterFormBean.getFilterValue(), filterFormBean.getFilterTypeId()));

        // Fix here!

        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=editFilter")
    public void editFilter(final ActionResponse response, @ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam("filter") final String filter,
            // @RequestParam("filter") final FilterType.Filter filter,
            @RequestParam("filterQuery") final String filterQuery, final Model model) throws IOException {

        model.addAttribute("newFilter", mapFieldInfToId().get(filter));
        model.addAttribute("filterValue", filterQuery);

        // Collection<String> vocabulary = metadataService.getVocabulary(filter.getMetadataKey());

        FieldInf newFilter = mapFieldInfToId().get(filter);

        System.out.println("\neditFilter: " + newFilter + "\n filter: " + filter + "\n");

        model.addAttribute("newFilter", newFilter);
        Collection<String> vocabulary = metadataService.getVocabulary(newFilter.getApelonKey());
        // Collection<String> vocabulary = metadataService.getVocabulary(filter);

        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson = new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);

        // Fix here!

        iFeed.removeFilter(new IFeedFilter(null, filterQuery, filter));

        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=removeFilter")
    public void removeFilter(final ActionResponse response, @ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam("filter") final FilterType.Filter filter,
            @RequestParam("filterQuery") final String filterQuery,
            @RequestParam("filterKey") final String filterKey, final Model model) {

        // Fix here!

        System.out.println("removeFilter " + filter + " filterQuery ");

        iFeed.removeFilter(new IFeedFilter(filter, filterQuery, filterKey));
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=cancel")
    public void cancel(final SessionStatus sessionStatus) {
        sessionStatus.setComplete();
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

    @ResourceMapping("findOrgs")
    public void searchOrg(@RequestParam String parentOrg, @RequestParam String url, ResourceResponse response)
            throws IOException {

        System.out.println("\nparentOrg: '" + parentOrg + "' Decoded: '" + URLDecoder.decode(parentOrg, "UTF-8")
                + "'");

        parentOrg = URLDecoder.decode(parentOrg, "UTF-8");

        System.out.println("\nUrl direkt från klienten " + url + "\n");
        url = URLDecoder.decode(url, "UTF-8");
        System.out.println("\nUrl efter decode från klienten " + url);

        VgrOrganization org = new VgrOrganization();
        org.setDn(parentOrg);

        String result = getVgrOrganizationJsonPart(org, url);
        final OutputStream out = response.getPortletOutputStream();
        out.write(result.getBytes("UTF-8"));
        out.flush();
        out.close();

        // List<VgrOrganization> orgs = ldapOrganizationService.findChildNodes(org);
        // addDataTo(orgs, url, "io");
        //
        // try {
        // final OutputStream out = response.getPortletOutputStream();
        // response.setContentType("application/json");
        // ObjectMapper om = new ObjectMapper();
        //
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // om.writeValue(baos, orgs);
        // baos.flush();
        // out.write(baos.toByteArray());
        // // om.writeValue(out, orgs);
        // out.flush();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    public void addDataTo(List<VgrOrganization> vos, String ns, String type) throws UnsupportedEncodingException {
        for (VgrOrganization vo : vos) {
            addDataTo(vo, ns, type);
        }
    }

    public void addDataTo(VgrOrganization vo, String portletUrl, String type) throws UnsupportedEncodingException {
        // ns = ns + "/findOrgs?parentOrg=";
        // if (ns.contains("parentOrg")) {
        // ns = ns.substring(0, ns.indexOf("parentOrg")) + "parentOrg";
        // }
        // vo.setIo(ns + "&parentOrg=" + URLEncoder.encode(vo.getDn(), "UTF-8") + "&url=" + ns);
        vo.setIo(portletUrl + "&parentOrg=" + URLEncoder.encode(vo.getDn(), "UTF-8") + "&url="
                + URLEncoder.encode(portletUrl, "UTF-8"));
        vo.setType("io");
        vo.setLeaf(ldapOrganizationService.findChildNodes(vo).isEmpty());
        vo.setType(type);
    }

    @ModelAttribute("filters")
    public List<Filter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(Filter.values()));
    }

    @ModelAttribute("filterTypes")
    public List<FilterType> getFilterTypes() {
        return Collections.unmodifiableList(Arrays.asList(FilterType.values()));
    }

    IFeedService getIFeedService() {
        return iFeedService;
    }

    void setIFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    public IFeedSolrQuery getIFeedSolrQuery() {
        return iFeedSolrQuery;
    }

    public void setIFeedSolrQuery(IFeedSolrQuery iFeedSolrQuery) {
        this.iFeedSolrQuery = iFeedSolrQuery;
    }

    public void setIFeedAtomFeed(UriTemplate iFeedAtomFeed) {
        this.iFeedAtomFeed = iFeedAtomFeed;
    }

    public UriTemplate getIFeedAtomFeed() {
        return iFeedAtomFeed;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public LdapPersonService getLdapPersonService() {
        return ldapPersonService;
    }

    public void setLdapPersonService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    public String getVgrOrganizationJson(String ns) throws JsonGenerationException, JsonMappingException,
            IOException {
        if (vgrOrganizationJsonCache.get() == null) {
            VgrOrganization org = new VgrOrganization();
            org.setDn("Ou=org");
            vgrOrganizationJsonCache = new WeakReference<String>(getVgrOrganizationJsonPart(org, ns));
        }
        return vgrOrganizationJsonCache.get();
    }

    private String getVgrOrganizationJsonPart(VgrOrganization org, String url2portlet)
            throws JsonGenerationException, JsonMappingException, IOException {

        List<VgrOrganization> orgs = getLdapOrganizationService().findChildNodes(org);

        if (orgs.isEmpty()) {
            VgrOrganization vo = new VgrOrganization();
            vo.setDn("test");
            vo.setOu("ouLabel");
            vo.setLeaf(false);
            orgs.add(vo);
        }

        addDataTo(orgs, url2portlet, "io");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectMapper().writeValue(baos, orgs);
        baos.flush();
        baos.close();

        String r = new String(baos.toByteArray(), "UTF-8");
        return r;

    }

    public LdapSupportService getLdapOrganizationService() {
        return ldapOrganizationService;
    }

    public void setLdapOrganizationService(LdapSupportService ldapOrganizationService) {
        this.ldapOrganizationService = ldapOrganizationService;
    }

    public UriTemplate getIFeedWebFeed() {
        return iFeedWebFeed;
    }

    public void setIFeedWebFeed(UriTemplate iFeedWebFeed) {
        this.iFeedWebFeed = iFeedWebFeed;
    }

    public List<FieldInf> getFieldInfs() {
        if (fieldInfs == null) {
            List<FieldsInf> infs = iFeedService.getFieldsInfs();
            fieldInfs = infs.get(infs.size() - 1).getFieldInfs();
        }

        System.out.println("anropar getFieldInfs " + fieldInfs.size());

        return fieldInfs;
    }

    public static List<FieldInf> getFieldInfsCache() {
        return fieldInfs;
    }

    public static void setFieldInfsCache(List<FieldInf> fieldInfs) {
        EditIFeedController.fieldInfs = fieldInfs;
    }

}
