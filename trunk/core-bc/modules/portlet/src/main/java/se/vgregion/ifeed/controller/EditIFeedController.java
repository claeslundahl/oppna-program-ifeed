package se.vgregion.ifeed.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
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
@SessionAttributes({ "ifeed", "hits" })
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
    @Autowired
    private UriTemplate iFeedAtomFeed;

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

        model.addAttribute("hits", new SearchResultList(iFeedSolrQuery.getIFeedResults(iFeed)));
        model.addAttribute("atomFeedLink",
                iFeedAtomFeed.expand(iFeed.getId(), iFeed.getSortField(), iFeed.getSortDirection()));
        // model.addAttribute("atomFeedLink", String.format(ifeedAtomFeed, iFeed.getId()));
        final PortletURL portletUrl = repsonse.createRenderURL();
        portletUrl.setParameter("view", "showEditIFeedForm");
        model.addAttribute("portletUrl", portletUrl);
        model.addAttribute("orderByCol", iFeed.getSortField());
        model.addAttribute("orderByType", iFeed.getSortDirection());
        return "editIFeedForm";
    }

    @ActionMapping(params = "action=saveIFeed")
    public void editIFeed(@ModelAttribute("ifeed") final IFeed iFeed, final ActionResponse response,
            final Model model, PortletRequest request) throws SystemException, PortalException {
        User user = PortalUtil.getUser(request);
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
    public void selectNewFilter(@ModelAttribute("ifeed") final IFeed iFeed, @RequestParam(required = false, value = "filter") final Filter filter,
            final Model model, final ActionResponse response) throws IOException {

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

    // @ResourceMapping
    public void searchPeople(@RequestParam final String filterValue, ResourceResponse response) {
        System.out.println("EditIFeedController.searchPeople()");
        List<Person> people = ldapPersonService.getPeople(filterValue, 10);
        try {
            final OutputStream out = response.getPortletOutputStream();
            response.setContentType("application/json");
            new ObjectMapper().writeValue(out, people);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ModelAttribute("filters")
    public List<Filter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(Filter.values()));
    }

    @ModelAttribute("filterTypes")
    public List<FilterType> getFilterTypes() {
        return Collections.unmodifiableList(Arrays.asList(FilterType.values()));
    }

    IFeedService getiFeedService() {
        return iFeedService;
    }

    void setiFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }
}
