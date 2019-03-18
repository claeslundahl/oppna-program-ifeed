package se.vgregion.ifeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import se.vgregion.ifeed.service.ifeed.IFeedService;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "fields" })
public class AdminFieldsController {

    @Autowired
    private IFeedService iFeedService;

    public AdminFieldsController() {

    }

    /*@ModelAttribute("fields")
    public FieldsInf getCommandObject() {
        List<FieldsInf> infs = iFeedService.getFieldsInfs();
        if (infs.isEmpty()) {
            return new FieldsInf();
        }
        return infs.get(infs.size() - 1);
    }

    @RenderMapping(params = { "view=showAdminFields" })
    public String rm(final RenderResponse response) {
        return "adminFields";
    }

    @ActionMapping(params = { "action=adminFields" })
    public void am(final ActionResponse response, final SessionStatus sessionStatus, final Model model) {
        response.setRenderParameter("view", "showAdminFields");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ActionMapping(params = { "action=addFieldConfig" })
    public void save(@ModelAttribute(value = "fields") FieldsInf fields, final ActionResponse response,
            final SessionStatus sessionStatus, final Model model) {

        fields.getFieldInfs(); // Throws exception and aborts the save if the text field cannot be parsed to
                               // FieldInf-objects.

        iFeedService.storeFieldsInf(fields);
        EditIFeedController.setFieldInfsCache(null);
        response.setRenderParameter("view", "showAdminFields");
    }*/

}
