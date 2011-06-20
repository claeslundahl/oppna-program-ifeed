package se.vgregion.ifeed.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortOrder;
import se.vgregion.ifeed.types.IFeed;

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
    public String showIFeedsDefault(Model model, @RequestParam(required=false) String orderByCol, @RequestParam(required=false, defaultValue="desc") String orderByType, RenderRequest request) {
        return showUserIFeeds(model, orderByCol, orderByType, request);
    }

    @SuppressWarnings("unchecked")
    @RenderMapping(params = { "view=showUserIFeeds" })
    public String showUserIFeeds(Model model, @RequestParam(required=false, defaultValue="name") String orderByCol, @RequestParam(required=false, defaultValue="desc") String orderByType, RenderRequest request) {
        SortOrder sortOrder = SortOrder.valueOf(orderByType);
        List<IFeed> userIFeeds = new ArrayList<IFeed>(iFeedService.getUserIFeeds(getRemoteUserId(request)));

        BeanComparator sortComparator = new BeanComparator(orderByCol);

        if(orderByType.equals(SortOrder.desc)) {
            Collections.sort(userIFeeds, Collections.reverseOrder(sortComparator));
        } else {
            Collections.sort(userIFeeds, sortComparator);
        }

        model.addAttribute("orderByCol", orderByCol);
        model.addAttribute("orderByType", sortOrder.reverse());
        model.addAttribute("ifeeds", userIFeeds);
        return "index";
    }

    @ModelAttribute("atomFeedUrl")
    public String getIfeedAtomFeed() {
        return ifeedAtomFeed;
    }

    @RenderMapping(params = { "view=showAllIFeeds" })
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
