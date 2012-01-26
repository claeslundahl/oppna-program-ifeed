package se.vgregion.ifeed.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "ifeed", "hits", "vgrOrganizationJson" })
public class EditIFeedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditIFeedController.class);

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

    // @Autowired
    // private LdapSupportService ldapOrganizationService;
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
            @RequestParam(defaultValue = "") final String orderByType, final Model model, RenderResponse repsonse) {
        model.asMap().get("ifeed");

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
        // model.addAttribute("vgrOrganizationJson", getVgrOrganizationJson());

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
        User user = fetchUser(request);
        if (!AccessGuard.mayEditFeed(user, iFeed)) {
            throw new RuntimeException();
        }
        IFeed ifeed = iFeedService.updateIFeed(iFeed);
        model.addAttribute("ifeed", ifeed);
        model.addAttribute("saveAction", Boolean.TRUE);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=updateIFeed")
    public void updateIFeed(@ModelAttribute("ifeed") final IFeed iFeed, final ActionResponse response,
            final Model model) {
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=selectFilter")
    public void selectNewFilter(@ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam(required = false, value = "filter") final Filter filter, final Model model,
            final ActionResponse response) throws IOException {

        model.addAttribute("newFilter", filter);
        Collection<String> vocabulary = metadataService.getVocabulary(filter.getMetadataKey());
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

        iFeed.addFilter(new IFeedFilter(filterFormBean.getFilter(), filterFormBean.getFilterValue()));
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=editFilter")
    public void editFilter(final ActionResponse response, @ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam("filter") final FilterType.Filter filter,
            @RequestParam("filterQuery") final String filterQuery, final Model model) throws IOException {

        model.addAttribute("newFilter", filter);
        model.addAttribute("filterValue", filterQuery);

        Collection<String> vocabulary = metadataService.getVocabulary(filter.getMetadataKey());
        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson = new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);

        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=removeFilter")
    public void removeFilter(final ActionResponse response, @ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam("filter") final FilterType.Filter filter,
            @RequestParam("filterQuery") final String filterQuery, final Model model) {

        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
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
            System.out.println("Nu körs searchPeople " + filterValue);
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            new ObjectMapper().writeValue(out, people);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResourceMapping("findOrgs")
    public void searchOrg(@RequestParam final String parentOrg, ResourceResponse response, PortletResponse pr)
            throws UnsupportedEncodingException {

        // String ns = pr.getNamespace() + "findOrgs?parentOrg=";
        //
        // System.out.println("Nu körs searchOrg " + parentOrg);
        // VgrOrganization org = new VgrOrganization();
        // org.setDn(parentOrg);
        // List<VgrOrganization> orgs = ldapOrganizationService.findChildNodes(org);
        // for (VgrOrganization vo : orgs) {
        // vo.setIo(ns + URLEncoder.encode(vo.getDn(), "UTF-8"));
        // }
        // try {
        // final OutputStream out = response.getPortletOutputStream();
        // response.setContentType("application/json");
        // new ObjectMapper().writeValue(out, orgs);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    // { http://alloy.liferay.com/demos.php?demo=treeview
    // type: 'task',
    // checkName: 'customSelectName',
    // label: 'TaskNodeRoot',
    // leaf: false,
    // checked: true,
    // io: 'deploy/demos/tree-view/assets/tasks.html',
    // on: {
    // check: defCallback,
    // uncheck: defCallback
    // }
    // },

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

    // private WeakReference<String> vgrOrganizationJsonCache = new WeakReference<String>(null);
    //
    // public String getVgrOrganizationJson() {
    // if (vgrOrganizationJsonCache.get() == null) {
    // VgrOrganization org = new VgrOrganization();
    // org.setDn("Ou=org");
    // vgrOrganizationJsonCache = new WeakReference<String>(getVgrOrganizationJsonPart(org));
    // }
    // return vgrOrganizationJsonCache.get();
    // }
    //
    // private String getVgrOrganizationJsonPart(VgrOrganization org) {
    // VgrOrganization vgr = new VgrOrganization();
    // vgr.setDn("Ou=org");
    //
    // List<VgrOrganization> orgs = getLdapOrganizationService().findChildNodes(vgr);
    //
    // try {
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // new ObjectMapper().writeValue(baos, orgs);
    // baos.flush();
    // baos.close();
    // return new String(baos.toByteArray());
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // throw new RuntimeException();
    // }
    //
    // /* [ { label: 'Folder 1', children: [ { label: 'file' }, { label: 'file' }, { label: 'file' } ] }, { label:
    // * 'Folder 2', expanded: true, children: [ { label: 'file' }, { label: 'file' } ] }, { label: 'Folder 3',
    // * children: [ { label: 'file' } ] }, { label: 'Folder 4', expanded: true, children: [ { label: 'Folder 4-1',
    // * expanded: true, children: [ { label: 'file' } ] } ] } ] */
    //
    // public LdapSupportService getLdapOrganizationService() {
    // return ldapOrganizationService;
    // }

    // public void setLdapOrganizationService(LdapSupportService ldapOrganizationService) {
    // this.ldapOrganizationService = ldapOrganizationService;
    // }

    public UriTemplate getIFeedWebFeed() {
        return iFeedWebFeed;
    }

    public void setIFeedWebFeed(UriTemplate iFeedWebFeed) {
        this.iFeedWebFeed = iFeedWebFeed;
    }

}
