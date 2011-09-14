package se.vgregion.ifeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;

@Controller
@RequestMapping("VIEW")
public class RemoveIFeedController {
    private IFeedService iFeedService;

    @Autowired
    public RemoveIFeedController(final IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    @ActionMapping(params = "action=removeIFeed")
    public void removeBook(@RequestParam final Long feedId) {
        iFeedService.removeIFeed(feedId);
    }

    //	@ExceptionHandler({ Exception.class })
    public String handleException() {
        return "errorPage";
    }
}
