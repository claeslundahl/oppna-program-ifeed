package se.vgregion.ifeed.backingbeans;

import org.apache.commons.collections.BeanMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.types.VgrDepartment;

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
