package se.vgregion.ifeed.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.theme.ThemeDisplay;

public class RemoveIFeedControllerTest {

    @Test
    public void removeBook() throws PortalException, SystemException {
        IFeedService service = mock(IFeedService.class);
        ResourceLocalService resourceService = mock(ResourceLocalService.class);
        final User user = mock(User.class);
        when(user.getScreenName()).thenReturn("screenName");
        IFeed feed = new IFeed();
        feed.setUserId("screenName");
        when(service.getIFeed(100l)).thenReturn(feed);
        final ThemeDisplay themeDisplay = mock(ThemeDisplay.class);

        ActionRequest request = mock(ActionRequest.class);
        when(request.getAttribute(WebKeys.THEME_DISPLAY)).thenReturn(themeDisplay);

        RemoveIFeedController rifc = new RemoveIFeedController(service, resourceService) {
            @Override
            User getUser(ActionRequest request) throws PortalException, SystemException {
                return user;
            }

            @Override
            ThemeDisplay getThemeDisplay() {
                return themeDisplay;
            }
        };

        PortletRequest portletRequest = mock(PortletRequest.class);

        rifc.removeBook(100l, request, portletRequest, themeDisplay);

        Mockito.verify(service, times(1)).getIFeed(Matchers.anyLong());
        Mockito.verify(service, times(1)).getIFeed(Matchers.eq(100l));

        Mockito.verify(service, times(1)).removeIFeed(Matchers.anyLong());
        Mockito.verify(service, times(1)).removeIFeed(Matchers.eq(100l));
    }
}
