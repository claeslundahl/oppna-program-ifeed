package se.vgregion.ifeed.controller;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.theme.ThemeDisplay;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "ifeed" })
public class AddIFeedController {
	private IFeedService iFeedService;
	private ResourceLocalService resourceLocalService;

	@Autowired
	public AddIFeedController(final IFeedService iFeedService, final ResourceLocalService resourceLocalService) {
		super();
		this.iFeedService = iFeedService;
		this.resourceLocalService = resourceLocalService;
	}

	@ModelAttribute("ifeed")
	public IFeed getCommandObject() {
		return new IFeed();
	}

	@RenderMapping(params = { "view=showAddIFeedForm" })
	public String showAddIFeedForm(final RenderResponse response) {
		return "addIFeedForm";
	}

	@InitBinder("ifeed")
	public void initBinder(final WebDataBinder binder) {
		// TODO Add validators
	}

	// @ExceptionHandler({ Exception.class })
	public String handleException() {
		return "errorPage";
	}

	@ActionMapping(params = { "action=createNewIFeed" })
	public void createNewIFeed(final ActionResponse response, final SessionStatus sessionStatus, final Model model) {
		sessionStatus.setComplete();
		model.addAttribute("ifeed", new IFeed());
		response.setRenderParameter("view", "showAddIFeedForm");
	}

	@ActionMapping(params = { "action=addIFeed" })
	public void addIFeed(final Model model, @Valid @ModelAttribute(value = "ifeed") IFeed iFeedIn,
	        final BindingResult bindingResult, final ActionRequest request, final ActionResponse response) {
		if (!bindingResult.hasErrors()) {
			IFeed iFeed = new IFeed();
			iFeed.setName(iFeedIn.getName());
			iFeed.setDescription(iFeedIn.getDescription());
			iFeed.setUserId(getRemoteUserId(request));
			iFeed = iFeedService.addIFeed(iFeed);
			try {
				ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
				long companyId = themeDisplay.getCompanyId();
				long userId = themeDisplay.getUserId();
				resourceLocalService.addResources(companyId, 0, userId, IFeed.class.getName(), iFeed.getId()
				        .longValue(), false, false, true);
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addAttribute("ifeed", iFeed);
			response.setRenderParameter("view", "showEditIFeedForm");
		} else {
			response.setRenderParameter("view", "showAddIFeedForm");
		}
	}

	private String getRemoteUserId(PortletRequest request) {
		@SuppressWarnings("unchecked")
		Map<String, ?> userInfo = (Map<String, ?>) request.getAttribute(PortletRequest.USER_INFO);
		String userId = "";
		if (userInfo != null) {
			userId = (String) userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
		}
		return userId;
	}
}
