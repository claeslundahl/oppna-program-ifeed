package se.vgregion.ifeed.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.SessionStatus;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.theme.ThemeDisplay;

public class AddIFeedControllerTest {

	AddIFeedController controller;
	private IFeedService iFeedService;
	private ResourceLocalService resourceLocalService;
	private RenderResponse response;

	@Before
	public void setUp() throws Exception {
		iFeedService = mock(IFeedService.class);
		resourceLocalService = mock(ResourceLocalService.class);
		controller = new AddIFeedController(iFeedService, resourceLocalService) {
			@Override
			protected User fetchUser(PortletRequest request) throws PortalException, SystemException {
				User user = Mockito.mock(User.class);
				return user;
			}
		};
		response = mock(RenderResponse.class);
	}

	@Test
	public void getCommandObject() {
		IFeed feed = controller.getCommandObject();
	}

	@Test
	public void showAddIFeedForm() {
		String result = controller.showAddIFeedForm(response);
		assertEquals("addIFeedForm", result);
	}

	// @Test
	// public void initBinder() {
	// WebDataBinder binder = mock(WebDataBinder.class);
	// controller.initBinder(binder);
	// }

	@Test
	public void createNewIFeed() {
		SessionStatus sessionStatus = mock(SessionStatus.class);
		Model model = mock(Model.class);
		ActionResponse actionResponse = mock(ActionResponse.class);

		controller.createNewIFeed(actionResponse, sessionStatus, model);
		verify(model).addAttribute(eq("ifeed"), any(IFeed.class));
	}

	@Test
	public void testAddIFeed() {
		Model model = mock(Model.class);
		IFeed iFeedIn = mock(IFeed.class);
		BindingResult bindingResult = mock(BindingResult.class);
		ActionRequest request = mock(ActionRequest.class);
		ActionResponse actionResponse = mock(ActionResponse.class);
		ThemeDisplay themeDisplay = mock(ThemeDisplay.class);

		when((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).thenReturn(themeDisplay);
		IFeed newFeed = new IFeed();
		newFeed.setId(101l);
		when(iFeedService.addIFeed(any(IFeed.class))).thenReturn(newFeed);

		controller.addIFeed(model, iFeedIn, bindingResult, request, actionResponse);
		verify(model).addAttribute(eq("ifeed"), eq(newFeed));
	}

}
