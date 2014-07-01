package se.vgregion.ifeed.backingbeans;

import se.vgregion.ifeed.service.solr.DateFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@FacesConverter(value = "textDateConverter")
public class TextDateConverter implements Converter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null) {
            return null;
        }
/*
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
*/
        return value;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }

        return sdf.format(value);
    }
 
}