package se.vgregion.ifeed.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.formbean.FilterFormBean;
import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

@Controller(value = "editIFeedController")
@RequestMapping(value = "VIEW")
@SessionAttributes(value = "ifeed")
public class EditIFeedController {
    @Autowired
    @Qualifier("iFeedService")
    private IFeedService iFeedService;
    @Autowired
    private Validator iFeedValidator;

    @RenderMapping(params = "action=showEditIFeedForm")
    public String showEditIFeedForm(RenderResponse response, SessionStatus sessionStatus) {
        return "editIFeedForm";
    }

    @ActionMapping(params = "action=editIFeed")
    public void editIFeed(@ModelAttribute("ifeed") IFeed iFeed, BindingResult bindingResult,
            ActionResponse response, SessionStatus sessionStatus) {
        iFeedValidator.validate(iFeed, bindingResult);
        if (!bindingResult.hasErrors()) {
            iFeedService.updateIFeed(iFeed);
            response.setRenderParameter("action", "showIFeeds");
            sessionStatus.setComplete();
        } else {
            response.setRenderParameter("feedId", iFeed.getId().toString());
            response.setRenderParameter("action", "showEditIFeedForm");
        }
    }

    @ActionMapping(params = "action=addFilter")
    public void addFilter(@ModelAttribute("ifeed") IFeed iFeed, @ModelAttribute FilterFormBean filterFormBean, ActionResponse response) {
        iFeed.addFilter(new IFeedFilter(filterFormBean.getFilter(), filterFormBean.getFilterValue()));
        response.setRenderParameter("feedId", iFeed.getId().toString());
        response.setRenderParameter("action", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=removeFilter")
    public void addFilter(@ModelAttribute("ifeed") IFeed iFeed, ActionResponse response) {
        //        iFeed.removeFilter(filter);
        response.setRenderParameter("feedId", iFeed.getId().toString());
        response.setRenderParameter("action", "showEditIFeedForm");
    }

    @ActionMapping(params = "action=saveIFeed")
    public void saveIFeed(@ModelAttribute("ifeed") IFeed iFeed) {
        iFeedService.updateIFeed(iFeed);
    }

    @InitBinder("ifeed")
    public void initBinder(WebDataBinder binder) {
        //
    }

    @ModelAttribute("ifeed")
    public IFeed getIFeed(@RequestParam Long feedId) {
        return iFeedService.getIFeed(feedId);
    }

    @ModelAttribute("filterTypes")
    public List<FilterType> getFilterTypes() {
        return Collections.unmodifiableList(Arrays.asList(FilterType.values()));
    }

    @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }
}
