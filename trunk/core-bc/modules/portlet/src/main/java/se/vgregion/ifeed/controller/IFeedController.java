package se.vgregion.ifeed.controller;

import static java.lang.Math.*;

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
    public String showIFeeds(Model model, @RequestParam(required = false, defaultValue = "10") int delta,
            @RequestParam(required = false, defaultValue = "1") int cur,
            @RequestParam(required = false) String orderByCol,
            @RequestParam(required = false, defaultValue = "asc") SortOrder orderByType, RenderRequest request) {

        String currentView = (String) model.asMap().get("currentView");
        if (currentView == null || currentView.equalsIgnoreCase("viewMine")) {
            return showUserIFeeds(model, delta, cur, orderByCol, orderByType, request);
        }
        return showAllIFeeds(model, delta, cur, orderByCol, orderByType);
    }

    @RenderMapping(params = { "view=showUserIFeeds" })
    public String showUserIFeeds(Model model, @RequestParam(required = false, defaultValue = "10") int delta,
            @RequestParam(required = false, defaultValue = "1") int cur,
            @RequestParam(required = false, defaultValue = "name") String orderByCol,
            @RequestParam(required = false, defaultValue = "asc") SortOrder orderByType, RenderRequest request) {

        List<IFeed> allIFeeds = new ArrayList<IFeed>(iFeedService.getUserIFeeds(getRemoteUserId(request)));
        handleViewSort(allIFeeds, orderByCol, orderByType);
        List<IFeed> truncatedIFeeds = handleViewPagination(allIFeeds, delta, cur);
        populateModel(model, truncatedIFeeds, allIFeeds.size(), delta, orderByCol, orderByType, "viewMine");
        return "index";
    }

    @RenderMapping(params = { "view=showAllIFeeds" })
    public String showAllIFeeds(Model model, @RequestParam(required = false, defaultValue = "10") int delta,
            @RequestParam(required = false, defaultValue = "1") int cur,
            @RequestParam(required = false, defaultValue = "name") String orderByCol,
            @RequestParam(required = false, defaultValue = "asc") SortOrder orderByType) {
        List<IFeed> allIFeeds = new ArrayList<IFeed>(iFeedService.getIFeeds());

        handleViewSort(allIFeeds, orderByCol, orderByType);
        List<IFeed> truncatedIFeeds = handleViewPagination(allIFeeds, delta, cur);
        populateModel(model, truncatedIFeeds, allIFeeds.size(), delta, orderByCol, orderByType, "viewAll");

        return "index";
    }

    private void populateModel(Model model, List<IFeed> iFeeds, int numberOfIfeeds, int delta, String orderCol, SortOrder orderType, String viewName) {
        model.addAttribute("numberOfIfeeds", numberOfIfeeds);
        model.addAttribute("ifeeds", iFeeds);
        model.addAttribute("delta", delta);
        model.addAttribute("orderByCol", orderCol);
        model.addAttribute("orderByType", orderType);
        model.addAttribute("toolbarItem", viewName);
        model.addAttribute("currentView", viewName);
    }

    @SuppressWarnings("unchecked")
    private List<IFeed> handleViewSort(List<IFeed> viewList, String orderByCol, SortOrder orderByType) {

        BeanComparator sortComparator = new BeanComparator(orderByCol);

        if (orderByType.equals(SortOrder.desc)) {
            Collections.sort(viewList, Collections.reverseOrder(sortComparator));
        } else {
            Collections.sort(viewList, sortComparator);
        }

        return viewList;
    }

    private List<IFeed> handleViewPagination(List<IFeed> viewList, int delta, int pageNumber) {
        int startIndex = delta * (pageNumber - 1);
        int totalSize = viewList.size();
        int endIndex = startIndex + delta;
        List<IFeed> subList = viewList.subList(startIndex, min(totalSize, endIndex));

        System.out.printf("Start: %s, End: %s, Total: %s", startIndex, endIndex, totalSize);
        System.out.println("size: " + subList.size());
        System.out.println("List: " + subList);
        return subList;
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
