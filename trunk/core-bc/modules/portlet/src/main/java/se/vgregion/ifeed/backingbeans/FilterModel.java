package se.vgregion.ifeed.backingbeans;

import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import org.apache.commons.collections.BeanMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.controller.EditIFeedController;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.VgrDepartment;

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

    @Override
    public void setDepartment(VgrDepartment department) {
        if (department == null) {
            setGroup(null);
        }
        super.setDepartment(department);
    }

    public void clear() {
        BeanMap bm = new BeanMap(this);
        for (Object key : bm.keySet()) {
            String name = (String) key;
            if (bm.getWriteMethod(name) != null) {
                try {
                    bm.put(name, null);
                } catch (Exception e) {

                }
            }
        }
    }

}
