package se.vgregion.ifeed.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import se.vgregion.ifeed.types.FieldInf;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({"ifeed", "hits", "vgrOrganizationJson", "fields"})
public class EditIFeedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditIFeedController.class);

    private static List<FieldInf> fieldInfs = new ArrayList<FieldInf>();

}
