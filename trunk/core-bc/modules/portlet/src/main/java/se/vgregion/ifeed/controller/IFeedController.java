package se.vgregion.ifeed.controller;

import java.util.List;

import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.types.IFeed;

@Controller
@RequestMapping(value = "VIEW")
public class IFeedController {

    private IFeedService iFeedService;

    @Autowired
    public IFeedController(IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    @RenderMapping
    public String showIFeeds(RenderResponse response) {
        return "index";
    }

    // @ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }

    @ModelAttribute(value = "ifeeds")
    public List<IFeed> getIFeeds() {
        return iFeedService.getIFeeds();
    }
}
