package se.vgregion.ifeed.types;

import org.apache.commons.lang.StringUtils;
import se.vgregion.common.utils.BeanMap;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FieldInf implements Serializable {

    public FieldInf() {
        super();
    }

    public FieldInf(List<FieldInf> children) {
        this();
        this.children.addAll(children);
        init();
    }

    private int level;

    private String id, name, help, type, apelonKey = "", value;

    private Set<String> counterparts = new HashSet<>();

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
        /*BeanMap bm = new BeanMap(this);
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
        BeanMap bm = new BeanMap(this);
        List<String> values = new ArrayList<>();
        for (Object o : getCsvBeanKeys()) {
            values.add((String) o);
        }
        return String.join(";", values) + "\n" +
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
        BeanMap bm = new BeanMap(this);
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
        BeanMap bm = new BeanMap(result);
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
        List<String> result = new ArrayList<>(new BeanMap(new FieldInf()).keySet());
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

    private void getAllIds(Set<String> result) {
        if (id != null) {
            result.add(id);
        }
        for (FieldInf child : children) {
            child.getAllIds(result);
        }
    }

    public Set<String> getAllIds() {
        Set<String> result = new TreeSet<>();
        getAllIds(result);
        result.remove("");
        return result;
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

    public void removeChildrenHavingNoHtmlValue() {
        if (parent != null && children.isEmpty() && !inHtmlView) {
            parent.getChildren().remove(this);
            return;
        }
        for (FieldInf child : new ArrayList<FieldInf>(children)) {
            child.removeChildrenHavingNoHtmlValue();
        }
    }

    public void removeChildrenHavingNoTooltipValue() {
        if (parent != null && !this.inTooltip) {
            parent.getChildren().remove(this);
            return;
        }
        for (FieldInf child : new ArrayList<FieldInf>(children)) {
            child.removeChildrenHavingNoTooltipValue();
        }
    }

    public void visit(Visitor visitor) {
        visitor.each(this);
        for (FieldInf child : new ArrayList<>(children)) {
            child.visit(visitor);
        }
    }

    public void initForMiniView(Map<String, Object> doc) {
        removeChildrenHavingNoTooltipValue();
        combine(doc);
        removeChildrenHavingNoValue();
        editTexts();
    }

    public void initForMaxView(Map<String, Object> doc) {
        removeChildrenHavingNoHtmlValue();
        for (FieldInf child : new ArrayList<>(children)) {
            if (!child.isInHtmlView()) {
                children.remove(child);
            }
        }
        combine(doc);
        removeChildrenHavingNoValue();
        editTexts();
    }

    private void editTexts() {
        visit(new FieldInf.Visitor() {
            @Override
            public void each(FieldInf item) {
                if (item.getName() != null) {
                    item.setName(
                            item.getName()
                                    .replace("(autokomplettering)", "")
                                    .replace("(VGR:s organisationsträd)", "")
                    );
                }
            }
        });
    }

    public Set<String> getCounterparts() {
        return counterparts;
    }

    public void setCounterparts(Set<String> counterparts) {
        this.counterparts = counterparts;
    }

    public interface Visitor {
        void each(FieldInf item);
    }

    public FieldInf toDetachedCopy() {
        FieldInf result = new FieldInf();
        BeanMap self = new BeanMap(this);
        BeanMap bm = new BeanMap(result);
        bm.putAllApplicable(self);
        result.setParent(null);
        return result;
    }

    public String[] toSolrKeys() {
        List<String> result = new ArrayList<>();
        result.add(id);
        result.addAll(counterparts);
        return result.toArray(new String[result.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldInf) {
            FieldInf fi = (FieldInf) obj;
            if (fi.id == id) {
                return true;
            }
            if (fi.id == null || id == null) {
                return false;
            }
            return fi.id.equals(id);
        }
        return false;
    }

/*    public static String[] toIdsList(Collection<FieldInf> fromThese) {
        Set<String> result = new HashSet<>();
        for (FieldInf fi : fromThese) {
            fi.visit(item -> {
                result.add(item.getId());
                result.addAll(item.getCounterparts());
            });
        }
        return result.toArray(new String[result.size()]);
    }*/

    public static String[] toIdsList(Collection<FieldInf> fromThese, String... withKeys) {
        return toIdsList(fromThese, Arrays.asList(withKeys));
    }

    public static String[] toIdsList(Collection<FieldInf> fromThese, Iterable<String> withKeys) {
        final Set<String> result = new HashSet<>();

        final List<String> asList = StreamSupport.stream(withKeys.spliterator(), false)
                .collect(Collectors.toList());

        for (FieldInf fi : fromThese) {
            fi.visit(item -> {
                if (asList.contains(item.getId()) || result.contains(item.getId())) {
                    result.add(item.getId());
                    result.addAll(item.getCounterparts());
                }
            });
        }

        result.addAll(asList);

        return result.toArray(new String[result.size()]);
    }

}
