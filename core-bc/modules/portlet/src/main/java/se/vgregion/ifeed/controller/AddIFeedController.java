package se.vgregion.ifeed.controller;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({"ifeed"})
public class AddIFeedController {
    private IFeedService iFeedService;

    @Autowired
    public AddIFeedController(final IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    @ModelAttribute("ifeed")
    public IFeed getCommandObject() {
        return new IFeed();
    }

    @RenderMapping(params = {"view=showAddIFeedForm"})
    public String showAddIFeedForm(final RenderResponse response) {
        return "addIFeedForm";
    }

    @InitBinder("ifeed")
    public void initBinder(final WebDataBinder binder) {
        //TODO Add validators
    }

    // @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }

    @ActionMapping(params = {"action=addIFeed"})
    public void addIFeed(final Model model,
            @Valid @ModelAttribute(value = "ifeed") IFeed iFeed,
            final BindingResult bindingResult, final ActionRequest request,
            final ActionResponse response) {
        if (!bindingResult.hasErrors()) {
            iFeed.setUserId(getRemoteUserId(request));
            iFeed = iFeedService.addIFeed(iFeed);
            model.addAttribute("ifeed", iFeed);
            response.setRenderParameter("view", "showEditIFeedForm");
        } else {
            response.setRenderParameter("view", "showAddIFeedForm");
        }
    }

    private String getRemoteUserId(PortletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, ?> userInfo = (Map<String, ?>) request.getAttribute(
                PortletRequest.USER_INFO);
        String userId = "";
        if (userInfo != null) {
            userId = (String) userInfo.get(
                    PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }
}
