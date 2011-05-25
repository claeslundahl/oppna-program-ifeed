package se.vgregion.ifeed.controller;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.types.IFeed;

@Controller
@RequestMapping("VIEW")
public class AddIFeedController {
    private IFeedService iFeedService;

    @Autowired
    public AddIFeedController(IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    @ModelAttribute("ifeed")
    public IFeed getCommandObject() {
        return new IFeed();
    }

    @RenderMapping(params = {"view=showAddIFeedForm"})
    public String showAddIFeedForm(RenderResponse response) {
        System.out.println("AddIFeedController.showAddIFeedForm()");
        return "addIFeedForm";
    }

    @InitBinder("ifeed")
    public void initBinder(WebDataBinder binder) {
        //TODO Add validators
    }

    // @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }

    @ActionMapping(params = {"action=addIFeed"})
    public void addIFeed(@Valid @ModelAttribute(value = "ifeed") IFeed iFeed, BindingResult bindingResult,
            ActionRequest request, ActionResponse response, SessionStatus sessionStatus) {
        if (!bindingResult.hasErrors()) {
            iFeed.setUserId(getRemoteUserId(request));
            iFeedService.addIFeed(iFeed);
            response.setRenderParameter("view", "showAllIFeeds");
            sessionStatus.setComplete();
        } else {
            response.setRenderParameter("view", "showAddIFeedForm");
        }
    }

    private String getRemoteUserId(PortletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, ?> userInfo = (Map<String, ?>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";
        if (userInfo != null) {
            userId = (String) userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        System.out.println("userId: " + userId);
        return userId;
    }

}
