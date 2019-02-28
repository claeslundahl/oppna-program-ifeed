package se.vgregion.ifeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import se.vgregion.ifeed.service.ifeed.IFeedService;

@Controller
@RequestMapping("VIEW")
public class RemoveIFeedController {
    private IFeedService iFeedService;
//    private ResourceLocalService resourceLocalService;

    public RemoveIFeedController() {

    }

    @Autowired
    public RemoveIFeedController(final IFeedService iFeedService/*, final ResourceLocalService resourceLocalService*/) {
        this.iFeedService = iFeedService;
//        this.resourceLocalService = resourceLocalService;
    }

    /*User getUser(ActionRequest request) throws PortalException, SystemException {
        return PortalUtil.getUser(request);
    }

    @ActionMapping(params = "action=removeIFeed")
    public void removeBook(@RequestParam final Long feedId, final ActionRequest request, final PortletRequest pr)
        throws PortalException, SystemException {
        User user = getUser(request);
        IFeed feed = iFeedService.getIFeed(feedId);
        if (!AccessGuard.mayEditFeed(user, feed)) {
            throw new RuntimeException();
        }
        iFeedService.removeIFeed(feedId);
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            long companyId = themeDisplay.getCompanyId();
            resourceLocalService.deleteResource(companyId, IFeed.class.getName(),
                ResourceConstants.SCOPE_INDIVIDUAL, feedId);
        } catch (PortalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/
}
