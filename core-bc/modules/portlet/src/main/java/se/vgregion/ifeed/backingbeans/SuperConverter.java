package se.vgregion.ifeed.backingbeans;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.ifeed.types.VgrGroup;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class SuperConverter<T extends AbstractEntity> implements Converter {

    //private Map<Long, T> cache = new HashMap<Long, T>();

    private static final WeakHashMap cache = new WeakHashMap();

    protected String ownClassName = getClass().getSimpleName();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null || "-".equals(value)) {
            return null;
        }

        //String id = ownClassName + Long.parseLong(value);
        String id = toKey(value);

        if (!cache.containsKey(id)) {
            /*VgrDepartment vgrDepartment = new VgrDepartment();
            vgrDepartment.setId(Long.parseLong(value));
            return vgrDepartment;*/
            throw new NullPointerException("Did not find a matching entity for id " + id + " in cache.");
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
        T department = (T) value;
        String id = toKey(department.getId());
        cache.put(id, department);
        return department.getId() + "";
    }

    private String toKey(Object id) {
        return ownClassName + ":" + id;
    }

}