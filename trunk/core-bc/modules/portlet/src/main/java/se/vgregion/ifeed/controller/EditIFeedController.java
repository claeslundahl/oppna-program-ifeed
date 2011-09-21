package se.vgregion.ifeed.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import se.vgregion.ifeed.formbean.FilterFormBean;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "ifeed", "hits" })
@Transactional
public class EditIFeedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            EditIFeedController.class);

    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private IFeedSolrQuery iFeedSolrQuery;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private LdapPersonService ldapPersonService;


    @Value("${ifeed.feed}")
    private String ifeedAtomFeed;

    @ActionMapping(params = "action=editIFeed")
    public void editIFeed(@RequestParam(required = true) final Long feedId,
            final Model model, final ActionResponse response,
            final SessionStatus sessionStatus) {
        IFeed iFeed = iFeedService.getIFeed(feedId);
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @RenderMapping(params = "view=showEditIFeedForm")
    public String showEditIFeedForm(
            @ModelAttribute("ifeed") final IFeed iFeed, final Model model) {
        model.addAttribute("hits", iFeedSolrQuery.getIFeedResults(iFeed));
        model.addAttribute("atomFeedLink", String.format(
                ifeedAtomFeed, iFeed.getId()));
        return "editIFeedForm";
    }

    @ActionMapping(params = "action=saveIFeed")
    public void editIFeed(@ModelAttribute("ifeed") final IFeed iFeed,
            final BindingResult bindingResult,
            final ActionResponse response, final Model model) {
        iFeedService.updateIFeed(iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=selectFilter")
    public void selectNewFilter(
            @RequestParam(required = false, value = "filter")
            final Filter filter, final Model model,
            final ActionResponse response)
                    throws IOException {

        model.addAttribute("newFilter", filter);
        Collection<String> vocabulary = metadataService.getVocabulary(
                filter.getMetadataKey());
        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson =
                new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);
        model.addAttribute("filterFormBean", new FilterFormBean());
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=addFilter")
    public void addFilter(@ModelAttribute("ifeed") final IFeed iFeed,
            @ModelAttribute final FilterFormBean filterFormBean,
            final ActionResponse response, final Model model) {

        LOGGER.debug("FilterFormBean: {}", ToStringBuilder.reflectionToString(filterFormBean));

        iFeed.addFilter(new IFeedFilter(filterFormBean.getFilter(),
                filterFormBean.getFilterValue()));
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=editFilter")
    public void editFilter(final ActionResponse response,
            @ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam("filter") final FilterType.Filter filter,
            @RequestParam("filterQuery") final String filterQuery,
            final Model model) throws IOException {

        model.addAttribute("newFilter", filter);
        model.addAttribute("filterValue", filterQuery);

        Collection<String> vocabulary = metadataService.getVocabulary(
                filter.getMetadataKey());
        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson =
                new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);

        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=removeFilter")
    public void removeFilter(final ActionResponse response,
            @ModelAttribute("ifeed") final IFeed iFeed,
            @RequestParam("filter") final FilterType.Filter filter,
            @RequestParam("filterQuery") final String filterQuery,
            final Model model) {

        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=cancel")
    public void cancel(final ActionResponse response,
            final SessionStatus sessionStatus, final Model model) {

        sessionStatus.setComplete();
    }

    @ResourceMapping
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

    @ModelAttribute("filterTypes")
    public List<FilterType> getFilterTypes() {
        return Collections.unmodifiableList(Arrays.asList(FilterType.values()));
    }

    // @ExceptionHandler({ Exception.class })
    // public String handleException() {
    // return "errorPage";
    // }
}
