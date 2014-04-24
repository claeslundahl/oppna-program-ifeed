package se.vgregion.ifeed.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

public class FieldInf {

    private String id, name, help, type, apelonKey = "";

    private final List<FieldInf> children = new ArrayList<FieldInf>();

    private boolean filter, inHtmlView, expanded;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        BeanMap bm = BeanMap.create(this);
        Map<?, ?> outer = new HashMap<Object, Object>(bm);
        return outer.toString();
    }

    public boolean isLabel() {
        return (isBlanc(id) && !isBlanc(name));
    }

    private boolean isBlanc(String s) {
        return (s == null || "".equals(s.trim()));
    }

    public List<FieldInf> getChildren() {
        return children;
    }

    public List<FieldInf> getFilterCriteriaTypes() {
        List<FieldInf> result = new ArrayList<FieldInf>();
        for (FieldInf fi : getChildren()) {
            if (fi.isFilter()) {
                result.add(fi);
            }
        }
        return result;
    }

    public List<FieldInf> getFilterCriteriaAndViewTypes() {
        List<FieldInf> result = new ArrayList<FieldInf>();
        for (FieldInf fi : getChildren()) {
            if (fi.isFilter() || fi.isInHtmlView()) {
                result.add(fi);
            }
        }
        return result;
    }

    public String getApelonKey() {
        return apelonKey;
    }

    public void setApelonKey(String apelonKey) {
        this.apelonKey = apelonKey;
    }

    public boolean isInHtmlView() {
        return inHtmlView;
    }

    public void setInHtmlView(boolean inHtmlView) {
        this.inHtmlView = inHtmlView;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}
