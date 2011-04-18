package se.vgregion.ifeed.controller;

import java.util.List;

import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.types.IFeed;

@Controller
@RequestMapping(value = "VIEW")
public class IFeedController {
	
	@Autowired
	@Qualifier("iFeedService")
	private IFeedService iFeedService;
	
	public void setIFeedService(IFeedService iFeedService) {
		this.iFeedService = iFeedService;
	}
	
	public IFeedService getIFeedService() {
		return iFeedService;
	}
	
	@RenderMapping
	public String showIFeeds(RenderResponse response) {
		return "index";
	}
	
	@ExceptionHandler({ Exception.class })
	public String handleException() {
		return "errorPage";
	}
	
	@ModelAttribute(value="ifeeds")
	public List<IFeed> getIFeeds() {
		return iFeedService.getIFeeds();
	}
}
