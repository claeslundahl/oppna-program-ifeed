package se.vgregion.ifeed.backingbeans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.controller.EditIFeedController;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.types.IFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-10.
 */
@Component(value = "filter")
@Scope("session")
public class FilterModel extends Filter {

}
