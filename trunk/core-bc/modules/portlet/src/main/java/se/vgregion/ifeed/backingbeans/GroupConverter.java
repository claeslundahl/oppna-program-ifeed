package se.vgregion.ifeed.backingbeans;

import se.vgregion.ifeed.types.VgrDepartment;
import se.vgregion.ifeed.types.VgrGroup;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "groupConverter")
public class GroupConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || "-".equals(value)) {
            return null;
        }

        VgrGroup vgrGroup = new VgrGroup();
        vgrGroup.setId(Long.parseLong(value));

        return vgrGroup;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) {
            return null;
        }
        VgrGroup group = (VgrGroup) value;
        return group.getId() + "";
    }
 
}