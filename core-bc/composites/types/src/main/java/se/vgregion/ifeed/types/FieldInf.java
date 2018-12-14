package se.vgregion.ifeed.types;

import net.sf.cglib.beans.BeanMap;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

public class FieldInf implements Serializable {

    public FieldInf() {
        super();
    }

    public FieldInf(List<FieldInf> children) {
        this();
        this.children.addAll(children);
    }

    private int level;


    private String id, name, help, type, apelonKey = "", value;

    private final List<FieldInf> children = new ArrayList<FieldInf>();

    private boolean filter, inHtmlView, expanded, inTooltip;

    private String operator;

    private transient FieldInf parent;

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
        return FieldInf.class.getSimpleName() + "(" + getId() + ")";
        /*BeanMap bm = BeanMap.create(this);
        Map<?, ?> outer = new HashMap<Object, Object>(bm);
        return outer.toString();*/
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

    /*public List<FieldInf> getFilterCriteriaTypes() {
        List<FieldInf> result = new ArrayList<FieldInf>();
        for (FieldInf fi : getChildren()) {
            if (fi.isFilter()) {
                result.add(fi);
            }
        }
        return result;
    }*/

    /*public List<FieldInf> getFilterCriteriaAndViewTypes() {
        List<FieldInf> result = new ArrayList<FieldInf>();
        for (FieldInf fi : getChildren()) {
            if (fi.isFilter() || fi.isInHtmlView()) {
                result.add(fi);
            }
        }
        return result;
    }*/

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public FieldInf getParent() {
        return parent;
    }

    public void setParent(FieldInf parent) {
        this.parent = parent;
    }

    public void init() {
        for (FieldInf child : children) {
            child.setParent(this);
            child.init();
        }
    }

    public boolean isInTooltip() {
        return inTooltip;
    }

    public void setInTooltip(boolean inTooltip) {
        this.inTooltip = inTooltip;
    }


    public String toCsvText() {
        BeanMap bm = BeanMap.create(this);
        List<String> values = new ArrayList<>();
        for (Object o : getCsvBeanKeys()) {
            values.add((String) o);
        }
        return StringUtils.join(values, ";") + "\n" +
                toCsvText(0);
    }

    private String toCsvText(int level) {
        this.level = level;
        StringBuilder sb = new StringBuilder();
        sb.append(toCsvRow());
        sb.append("\n");
        for (FieldInf child : children) {
            sb.append(child.toCsvText(level + 1));
        }
        return sb.toString();
    }

    public static FieldInf fromCsvText(List<String> content) {
        return null;
    }

    public String toCsvRow() {
        List<String> values = new ArrayList<>();
        BeanMap bm = BeanMap.create(this);
        for (String o : getCsvBeanKeys()) {
            Object value = bm.get(o);
            if (value == null) {
                values.add("");
            } else {
                values.add(String.valueOf(value));
            }
        }
        return (StringUtils.join(values, ";"));
    }

    public static FieldInf fromCsvRow(String text) {
        FieldInf result = new FieldInf();
        BeanMap bm = BeanMap.create(result);
        String[] parts = text.split(Pattern.quote(";"));
        int i = 0;
        for (String o : getCsvBeanKeys()) {
            String v = parts[i];
            if (v != null && !v.trim().isEmpty()) {
                bm.put(o, v);
            }
            i++;
        }
        return result;
    }

    static Collection<String> getCsvBeanKeys() {
        List<String> result = new ArrayList<>(BeanMap.create(new FieldInf()).keySet());
        result.remove("class");
        result.remove("children");
        result.remove("value");
        result.remove("expanded");
        result.remove("operator");
        return result;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void combine(Map<String, Object> doc) {
        if (doc.containsKey(id)) {
            Object v = doc.get(id);
            setValue(toString(v));
        }
        for (FieldInf child : children) {
            child.combine(doc);
        }
    }

    public String getDeepValue(String withId) {
        if (withId.equals(id)) {
            return value;
        }

        for (FieldInf child : children) {
            String result = child.getDeepValue(withId);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public void removeChildrenHavingNoValue() {
        for (FieldInf child : new ArrayList<>(children)) {
            if (!child.isHavingSomeValue()) {
                children.remove(child);
            }
        }
        for (FieldInf child : new ArrayList<>(children)) {
            child.removeChildrenHavingNoValue();
        }
    }

    public int childCount() {
        int result = children.size();

        for (FieldInf child : children) {
            result += child.childCount();
        }

        return result;
    }

    public boolean isHavingSomeValue() {
        if (value != null) {
            return true;
        }

        for (FieldInf child : children) {
            if (child.isHavingSomeValue()) {
                return true;
            }
        }

        return false;
    }

    private String toString(Object value) {
        if (value != null) {
            if (value instanceof String) {
                return (String) value;
            } else if (value instanceof Number) {
                return value + "";
            } else if (value instanceof List) {
                return (value + "").replace("[", "").replace("]", "");
            }
        }
        return null;
    }

}
