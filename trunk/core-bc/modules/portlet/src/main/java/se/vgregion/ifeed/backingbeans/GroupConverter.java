package se.vgregion.ifeed.backingbeans;

import se.vgregion.ifeed.types.VgrDepartment;
import se.vgregion.ifeed.types.VgrGroup;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.HashMap;
import java.util.Map;

@FacesConverter(value = "groupConverter")
public class GroupConverter /* extends SuperConverter<VgrGroup>*/ implements Converter {

    private static Map<Long, VgrGroup> cache = new HashMap<Long, VgrGroup>();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null || "-".equals(value)) {
            return null;
        }

        Long id = Long.parseLong(value);

        if (!cache.containsKey(id)) {
            throw new NullPointerException("Did not find a matching VgrGroup for id " + id + " in cache.");
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
        VgrGroup department = (VgrGroup) value;
        cache.put(department.getId(), department);
        return department.getId() + "";
    }
 
}