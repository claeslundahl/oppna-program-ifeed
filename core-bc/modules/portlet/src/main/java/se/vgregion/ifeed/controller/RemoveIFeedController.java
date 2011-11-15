package se.vgregion.ifeed.controller;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

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

    @ActionMapping(params = "action=removeIFeed")
    public void removeBook(@RequestParam final Long feedId, final ActionRequest request, final PortletRequest pr)
            throws PortalException, SystemException {
        User user = PortalUtil.getUser(request);
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
