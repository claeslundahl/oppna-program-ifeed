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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortOrder;
import se.vgregion.ifeed.types.IFeed;

@Controller
@RequestMapping("VIEW")
@SessionAttributes("currentView")
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
    public String showIFeeds(Model model, @RequestParam(required=false) String orderByCol, @RequestParam(required=false, defaultValue="asc") SortOrder orderByType, RenderRequest request) {
        String currentView = (String) model.asMap().get("currentView");
        System.out.println("IFeedController.showIFeeds()");
        System.out.println("currentView: " + currentView);
        if(currentView == null || currentView.equalsIgnoreCase("viewMine")) {
            return showUserIFeeds(model, orderByCol, orderByType, request);
        }
        return showAllIFeeds(model, orderByCol, orderByType);
    }

    @RenderMapping(params = { "view=showUserIFeeds" })
    public String showUserIFeeds(Model model, @RequestParam(required=false, defaultValue="name") String orderByCol, @RequestParam(required=false, defaultValue="asc") SortOrder orderByType, RenderRequest request) {
        System.out.println("IFeedController.showUserIFeeds()");
        List<IFeed> userIFeeds = new ArrayList<IFeed>(iFeedService.getUserIFeeds(getRemoteUserId(request)));
        handleViewSort(model, userIFeeds, orderByCol, orderByType);
        model.addAttribute("toolbarItem", "viewMine");
        model.addAttribute("currentView", "viewMine");
        return "index";
    }

    @RenderMapping(params = { "view=showAllIFeeds" })
    public String showAllIFeeds(Model model, @RequestParam(required=false, defaultValue="name") String orderByCol, @RequestParam(required=false, defaultValue="asc") SortOrder orderByType) {
        List<IFeed> userIFeeds = new ArrayList<IFeed>(iFeedService.getIFeeds());
        handleViewSort(model, userIFeeds, orderByCol, orderByType);
        model.addAttribute("toolbarItem", "viewAll");
        model.addAttribute("currentView", "view-All");
        return "index";
    }

    @SuppressWarnings("unchecked")
    private void handleViewSort(Model model, List<IFeed> viewList, String orderByCol, SortOrder orderByType) {

        BeanComparator sortComparator = new BeanComparator(orderByCol);

        if(orderByType.equals(SortOrder.desc)) {
            Collections.sort(viewList, Collections.reverseOrder(sortComparator));
        } else {
            Collections.sort(viewList, sortComparator);
        }

        model.addAttribute("ifeeds", viewList);
        model.addAttribute("orderByCol", orderByCol);
        model.addAttribute("orderByType", orderByType);
        model.addAttribute("ifeeds", viewList);
    }

    @ModelAttribute("atomFeedUrl")
    public String getIfeedAtomFeed() {
        return ifeedAtomFeed;
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
