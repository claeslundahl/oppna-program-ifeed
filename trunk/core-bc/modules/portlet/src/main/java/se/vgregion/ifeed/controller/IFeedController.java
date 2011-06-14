package se.vgregion.ifeed.controller;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;

@Controller
@RequestMapping("VIEW")
public class IFeedController {

    private IFeedService iFeedService;

    @Value("${ifeed.feed}")
    private String ifeedAtomFeed;

    @Autowired
    public IFeedController(IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    @RenderMapping
    public String showIFeedsDefault(Model model, RenderRequest request) {
        return showUserIFeeds(model, request);
    }

    @RenderMapping(params={"view=showUserIFeeds"})
    public String showUserIFeeds(Model model, RenderRequest request) {
        model.addAttribute("ifeeds", iFeedService.getUserIFeeds(getRemoteUserId(request)));
        return "index";
    }

    @ModelAttribute("atomFeedUrl")
    public String getIfeedAtomFeed() {
        return ifeedAtomFeed;
    }

    @RenderMapping(params={"view=showAllIFeeds"})
    public String showIFeeds(Model model) {
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
