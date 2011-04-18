package se.vgregion.ifeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

import se.vgregion.ifeed.service.IFeedService;

@Controller
@RequestMapping("VIEW")
public class RemoveIFeedController {
	@Autowired
	@Qualifier("iFeedService")
	private IFeedService iFeedService;

	@ActionMapping(params = "action=removeIFeed")
	public void removeBook(@RequestParam Long feedId) {
		iFeedService.removeIFeed(feedId);
	}

	@ExceptionHandler({ Exception.class })
	public String handleException() {
		return "errorPage";
	}
}
