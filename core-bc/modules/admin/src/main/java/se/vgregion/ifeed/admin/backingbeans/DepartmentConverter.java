package se.vgregion.ifeed.admin.backingbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.VgrDepartment;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FacesConverter(value = "departmentConverter")
public class DepartmentConverter implements Converter {

    private static Map<Long, VgrDepartment> cache = new HashMap<Long, VgrDepartment>();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || "-".equals(value)) {
            return null;
        }

        Long id = Long.parseLong(value);

        if (!cache.containsKey(id)) {
            /*VgrDepartment vgrDepartment = new VgrDepartment();
            vgrDepartment.setId(Long.parseLong(value));
            return vgrDepartment;*/
            throw new NullPointerException("Did not find a matching VgrDepartment for id " + id + " in cache.");
        } else {
            return cache.get(id);
        }
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) {
            return null;
        }
        VgrDepartment department = (VgrDepartment) value;
        cache.put(department.getId(), department);
        return department.getId() + "";
    }
 
}