package se.vgregion.ifeed.admin.backingbeans;


import net.sf.cglib.beans.BeanMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.common.utils.CommonUtils;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ifeed.types.Ownership;
import se.vgregion.ifeed.types.VgrDepartment;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Monica on 2014-03-28.
 */
@Component(value = "vgrDepartmentModel")
@Scope("session")
public class VgrDepartmentModel extends VgrDepartment implements Serializable {

    private static final long serialVersionUID = -2277251806545192509L;

    public VgrDepartmentModel() {
    }


    private void copy(Object from, Object into) {
        BeanMap fromMap = BeanMap.create(from);
        BeanMap intoMap = BeanMap.create(into);
        intoMap.putAll(fromMap);
    }

    public void copyValuesFrom(VgrDepartment department) {
        copy(department, this);
    }

}
