package se.vgregion.ifeed.controller;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.types.IFeed;

@Controller(value = "addIFeedController")
@RequestMapping(value = "VIEW")
@SessionAttributes(types = IFeed.class)
public class AddIFeedController {
    @Autowired
    @Qualifier("iFeedService")
    private IFeedService iFeedService;

    public void setBookService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    @ModelAttribute("ifeed")
    public IFeed getCommandObject() {
        return new IFeed();
    }

    @RenderMapping(params = "action=showAddIFeedForm")
    public String showAddIFeedForm(RenderResponse response) {

        return "addIFeedForm";
    }

    @InitBinder("ifeed")
    public void initBinder(WebDataBinder binder) {
        // binder.registerCustomEditor(Long.class, new
        // LongNumberPropertyEditor());
    }

    // @ExceptionHandler({ Exception.class })
    // public String handleException() {
    // return "errorPage";
    // }

    @ActionMapping(params = "action=addIFeed")
    public void addIFeed(@Valid @ModelAttribute(value = "ifeed") IFeed iFeed, BindingResult bindingResult,
            ActionResponse response, SessionStatus sessionStatus) {
        if (!bindingResult.hasErrors()) {
            iFeedService.addIFeed(iFeed);
            response.setRenderParameter("action", "showIFeeds");
            sessionStatus.setComplete();
        } else {
            response.setRenderParameter("action", "showAddIFeedForm");
        }
    }
}
