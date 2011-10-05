package se.vgregion.ifeed.controller;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.comparators.TransformingComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.util.UriTemplate;

import se.vgregion.common.utils.CommonUtils;
import se.vgregion.ifeed.formbean.IFeedFormBeanList;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.types.IFeed;

@Controller
@RequestMapping("VIEW")
@SessionAttributes("currentView")
public class IFeedController {

    private IFeedService iFeedService;
    private UriTemplate iFeedAtomFeed;

    @Autowired
    public IFeedController(IFeedService iFeedService, UriTemplate iFeedAtomFeed) {
        super();
        this.iFeedService = iFeedService;
        this.iFeedAtomFeed = iFeedAtomFeed;
    }

    @RenderMapping
    public String showIFeeds(final Model model,
            @RequestParam(defaultValue = "10") final int delta,
            @RequestParam(defaultValue = "1") final int cur,
            @RequestParam(defaultValue = "name") final String orderByCol,
            @RequestParam(defaultValue = "asc") final SortDirection orderByType,
            final RenderRequest request) {

        String currentView = (String) model.asMap().get("currentView");
        if (currentView == null || currentView.equalsIgnoreCase("viewMine")) {
            return showUserIFeeds(model, delta, cur, orderByCol,
                    orderByType, request);
        }
        return showAllIFeeds(model, delta, cur, orderByCol, orderByType);
    }

    @RenderMapping(params = { "view=showUserIFeeds" })
    public String showUserIFeeds(final Model model,
            @RequestParam(defaultValue = "10") final int delta,
            @RequestParam(defaultValue = "1") final int cur,
            @RequestParam(defaultValue = "name") final String orderByCol,
            @RequestParam(defaultValue = "asc") final SortDirection orderByType,
            final RenderRequest request) {

        List<IFeed> allIFeeds = new ArrayList<IFeed>(iFeedService.getUserIFeeds(
                getRemoteUserId(request)));
        handleViewSort(allIFeeds, orderByCol, orderByType);
        List<IFeed> truncatedIFeeds = handleViewPagination(
                allIFeeds, delta, cur);
        populateModel(model, truncatedIFeeds, allIFeeds.size(),
                delta, orderByCol, orderByType, "viewMine");
        return "index";
    }

    @RenderMapping(params = { "view=showAllIFeeds" })
    public String showAllIFeeds(final Model model,
            @RequestParam(defaultValue = "10") final int delta,
            @RequestParam(defaultValue = "1") final int cur,
            @RequestParam(defaultValue = "name") final String orderByCol,
            @RequestParam(defaultValue = "asc") final SortDirection orderByType) {

        List<IFeed> allIFeeds = iFeedService.getIFeeds();

        handleViewSort(allIFeeds, orderByCol, orderByType);
        List<IFeed> truncatedIFeeds = handleViewPagination(
                allIFeeds, delta, cur);
        populateModel(model, truncatedIFeeds, allIFeeds.size(),
                delta, orderByCol, orderByType, "viewAll");

        return "index";
    }

    private void populateModel(final Model model, final List<IFeed> iFeeds,
            final int numberOfIfeeds, final int delta, final String orderCol,
            final SortDirection orderType, final String viewName) {
        model.addAttribute("numberOfIfeeds", numberOfIfeeds);
        model.addAttribute("ifeeds", new IFeedFormBeanList(iFeeds, iFeedAtomFeed));
        model.addAttribute("delta", delta);
        model.addAttribute("orderByCol", orderCol);
        model.addAttribute("orderByType", orderType);
        model.addAttribute("toolbarItem", viewName);
        model.addAttribute("currentView", viewName);
    }

    private static final Transformer LOWER_CASE_TRANSFORMER = new Transformer() {
        @Override
        public Object transform(final Object input) {
            return ((String) input).toLowerCase(CommonUtils.SWEDISH_LOCALE);
        }
    };

    @SuppressWarnings("unchecked")
    private List<IFeed> handleViewSort(final List<IFeed> viewList,
            final String orderByCol, final SortDirection orderByType) {

        if (!CommonUtils.isNull(orderByCol)) {
            Comparator<IFeed> sortComparator = new BeanComparator(
                    orderByCol, new TransformingComparator(
                            LOWER_CASE_TRANSFORMER));

            if (orderByType.equals(SortDirection.desc)) {
                Collections.sort(viewList, Collections.reverseOrder(
                        sortComparator));
            } else {
                Collections.sort(viewList, sortComparator);
            }
        }
        return viewList;
    }

    private List<IFeed> handleViewPagination(List<IFeed> viewList,
            final int delta, final int pageNumber) {

        int startIndex = delta * (pageNumber - 1);
        int totalSize = viewList.size();
        int endIndex = startIndex + delta;
        List<IFeed> subList = viewList.subList(
                startIndex, min(totalSize, endIndex));

        return subList;
    }

    private String getRemoteUserId(PortletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, ?> userInfo =
        (Map<String, ?>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";
        if (userInfo != null) {
            userId = (String) userInfo.get(
                    PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }

    // @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }
}
