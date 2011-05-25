package se.vgregion.ifeed.controller;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.IFeedService;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "ifeed", "feedId", "hits"})
public class IFeedController {

    private IFeedService iFeedService;

    @Autowired
    public IFeedController(IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    @RenderMapping
    public String showIFeedsDefault(Model model, RenderRequest request) {
        System.out.println("IFeedController.showIFeedsDefault()");
        return showUserIFeeds(model, request);
    }

    @RenderMapping(params={"view=showUserIFeeds"})
    public String showUserIFeeds(Model model, RenderRequest request) {
        System.out.println("IFeedController.showUserIFeeds()");
        model.addAttribute("ifeeds", iFeedService.getUserIFeeds(getRemoteUserId(request)));
        return "index";
    }

    @RenderMapping(params={"view=showAllIFeeds"})
    public String showIFeeds(Model model) {
        System.out.println("IFeedController.showIFeeds()");
        model.addAttribute("ifeeds", iFeedService.getIFeeds());
        return "index";
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


    // @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }
}
