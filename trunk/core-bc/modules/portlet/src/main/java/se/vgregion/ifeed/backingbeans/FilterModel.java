package se.vgregion.ifeed.backingbeans;

import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.controller.EditIFeedController;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.types.IFeed;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-10.
 */
@Component(value = "filter")
@Scope("session")
public class FilterModel extends Filter {

}
