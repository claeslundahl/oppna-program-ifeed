package se.vgregion.ifeed.controller;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

@Controller
@RequestMapping("VIEW")
public class RemoveIFeedController {
    private IFeedService iFeedService;
    private ResourceLocalService resourceLocalService;

    @Autowired
    public RemoveIFeedController(final IFeedService iFeedService, final ResourceLocalService resourceLocalService) {
        this.iFeedService = iFeedService;
        this.resourceLocalService = resourceLocalService;
    }

    User getUser(ActionRequest request) throws PortalException, SystemException {
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
    }
}
