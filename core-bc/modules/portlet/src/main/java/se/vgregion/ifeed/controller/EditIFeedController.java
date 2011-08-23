package se.vgregion.ifeed.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.ActionResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.formbean.FilterFormBean;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portlet.calendar.model.CalEvent;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "ifeed", "hits" })
@Transactional
public class EditIFeedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditIFeedController.class);

    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private IFeedSolrQuery iFeedSolrQuery;
    @Autowired
    private Validator iFeedValidator;
    @Autowired
    private MetadataService metadataService;

    @Value("${ifeed.feed}")
    private String ifeedAtomFeed;

    @ActionMapping(params = "action=editIFeed")
    public void editIFeed(@RequestParam(required = true) Long feedId, Model model, ActionResponse response, SessionStatus sessionStatus) {
        IFeed iFeed = iFeedService.getIFeed(feedId);
        model.addAttribute("ifeed", iFeed);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @RenderMapping(params = "view=showEditIFeedForm")
    public String showEditIFeedForm(@ModelAttribute("ifeed") IFeed iFeed, Model model) {
        model.addAttribute("hits", iFeedSolrQuery.getIFeedResults(iFeed));
        model.addAttribute("atomFeedLink", String.format(ifeedAtomFeed, iFeed.getId()));
        return "editIFeedForm";
    }

    @ActionMapping(params = "action=saveIFeed")
    public void editIFeed(@ModelAttribute("ifeed") IFeed iFeed, BindingResult bindingResult,
            ActionResponse response, Model model) {
        iFeedValidator.validate(iFeed, bindingResult);
        if (!bindingResult.hasErrors()) {
            iFeedService.updateIFeed(iFeed);
        }
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=selectFilter")
    public void selectNewFilter(@RequestParam(required = false, value = "filter") Filter filter, Model model,
            ActionResponse response) throws IOException {
        model.addAttribute("newFilter", filter);
        Collection<String> vocabulary = metadataService.getVocabulary(filter.getMetadataKey());
        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson = new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);
        model.addAttribute("filterFormBean", new FilterFormBean());
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=addFilter")
    public void addFilter(@ModelAttribute("ifeed") IFeed iFeed, @ModelAttribute FilterFormBean filterFormBean,
            ActionResponse response, Model model) {
        iFeed.addFilter(new IFeedFilter(filterFormBean.getFilter(), filterFormBean.getFilterValue()));
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=editFilter")
    public void editFilter(ActionResponse response, @ModelAttribute("ifeed") IFeed iFeed,
            @RequestParam("filter") FilterType.Filter filter, @RequestParam("filterQuery") String filterQuery,
            Model model) throws IOException {

        model.addAttribute("newFilter", filter);
        model.addAttribute("filterValue", filterQuery);

        Collection<String> vocabulary = metadataService.getVocabulary(filter.getMetadataKey());
        model.addAttribute("vocabulary", vocabulary);
        String vocabularyJson = new ObjectMapper().writeValueAsString(vocabulary);
        model.addAttribute("vocabularyJson", vocabularyJson);

        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=removeFilter")
    public void removeFilter(ActionResponse response, @ModelAttribute("ifeed") IFeed iFeed,
            @RequestParam("filter") FilterType.Filter filter, @RequestParam("filterQuery") String filterQuery,
            Model model) {
        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=cancel")
    public void cancel(ActionResponse response, SessionStatus sessionStatus, Model model) {
        sessionStatus.setComplete();
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
