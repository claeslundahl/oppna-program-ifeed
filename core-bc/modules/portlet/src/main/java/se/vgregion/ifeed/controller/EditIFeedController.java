package se.vgregion.ifeed.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.formbean.FilterFormBean;
import se.vgregion.ifeed.metadata.service.MetadataService;
import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ifeed.types.Metadata;

@Controller(value = "editIFeedController")
@RequestMapping(value = "VIEW")
@SessionAttributes(value = { "ifeed", "feedId", "hits", "metadata" })
public class EditIFeedController {

    @Autowired
    @Qualifier("iFeedService")
    private IFeedService iFeedService;
    @Autowired
    @Qualifier("iFeedSolrQuery")
    private IFeedSolrQuery iFeedSolrQuery;
    @Autowired
    private Validator iFeedValidator;
//    @Autowired
//    MetadataService metadataService;

    @RenderMapping(params = "view=showEditIFeedForm")
    public String showEditIFeedForm(@ModelAttribute("ifeed") IFeed iFeed) {
        System.out.println("EditIFeedController.showEditIFeedForm()");
        return "editIFeedForm";
    }

    @ActionMapping(params = "action=editIFeed")
    public void editIFeed(@ModelAttribute("ifeed") IFeed iFeed, BindingResult bindingResult,
            ActionResponse response, SessionStatus sessionStatus, Model model) {
        iFeedValidator.validate(iFeed, bindingResult);
        if (!bindingResult.hasErrors()) {
            iFeedService.updateIFeed(iFeed);
            model.addAttribute("view", "showAllIFeeds");
            // response.setRenderParameter("view", "showAllIFeeds");
            sessionStatus.setComplete();
        } else {
            response.setRenderParameter("view", "showEditIFeedForm");
        }
    }

    @ActionMapping(params = "action=addFilter")
    public void addFilter(@ModelAttribute("ifeed") IFeed iFeed, @ModelAttribute FilterFormBean filterFormBean,
            ActionResponse response, Model model) {
        iFeed.addFilter(new IFeedFilter(filterFormBean.getFilter(), filterFormBean.getFilterValue()));
        List<Map<String, Object>> hits = iFeedSolrQuery.getIFeedResults(iFeed);
        model.addAttribute("hits", hits);
        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=removeFilter")
    public void removeFilter(ActionResponse response, @ModelAttribute("ifeed") IFeed iFeed,
            @RequestParam("filter") FilterType.Filter filter, @RequestParam("filterQuery") String filterQuery,
            Model model) {
        iFeed.removeFilter(new IFeedFilter(filter, filterQuery));
        List<Map<String, Object>> hits = iFeedSolrQuery.getIFeedResults(iFeed);
        model.addAttribute("hits", hits);

        response.setRenderParameter("view", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=saveIFeed")
    public void saveIFeed(@ModelAttribute("ifeed") IFeed iFeed) {
        iFeedService.updateIFeed(iFeed);
    }

//    @ModelAttribute("metadata")
//    public Map<String, Collection<Metadata>> getMetdata() {
//        Map<String, Collection<Metadata>> metadataMap = new HashMap<String, Collection<Metadata>>();
//        List<String> rootNodeNames = Arrays.asList("Handlingstyp", "Dokumentstatus", "Dokumenttyp VGR",
//        "Verksamhetskod");
//        Long start = System.currentTimeMillis();
//        for (String rootNodeName : rootNodeNames) {
//            collectMetadata(metadataMap, rootNodeName);
//        }
//        Long stop = System.currentTimeMillis();
//        System.out.println("Time taken: " + (stop - start));
//        return metadataMap;
//    }

//    private void collectMetadata(Map<String, Collection<Metadata>> metadataMap, String nodeName) {
//        Collection<Metadata> children = metadataService.getVocabulary(nodeName);
//        metadataMap.put(nodeName, children);
//        for (Metadata metadata : children) {
//            collectMetadata(metadataMap, metadata.getName());
//        }
//    }

    @ActionMapping(params = "action=cancel")
    public void cancel(ActionResponse response, SessionStatus sessionStatus, Model model) {
        sessionStatus.setComplete();
        model.addAttribute("view", "showAllIFeeds");
        response.setRenderParameter("view", "showAllIFeeds");
    }

    @InitBinder("ifeed")
    public void initBinder(WebDataBinder binder) {
        // TODO Add validators
    }

    @ModelAttribute("ifeed")
    public IFeed getIFeed(@RequestParam(required = false) Long feedId, Model model) {
        model.addAttribute("feedId", feedId);
        IFeed feed = iFeedService.getIFeed(feedId);
        List<Map<String, Object>> hits = iFeedSolrQuery.getIFeedResults(feed);
        model.addAttribute("hits", hits);
        return feed;
    }

    @ModelAttribute("filterTypes")
    public List<FilterType> getFilterTypes() {
        return Collections.unmodifiableList(Arrays.asList(FilterType.values()));
    }

    // @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }
}
