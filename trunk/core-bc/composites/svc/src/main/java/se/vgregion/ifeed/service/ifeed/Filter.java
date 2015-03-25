package se.vgregion.ifeed.service.ifeed;

import se.vgregion.ifeed.types.IFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-10.
 */
public class Filter extends IFeed {

    private String idAsText;

    public String toJpqlQuery(List<Object> values) {
        StringBuilder sb = new StringBuilder();

        sb.append("select o from " + IFeed.class.getSimpleName() + " o");
        List condition = new ArrayList<Object>();

        addConditionIfAnyValue("o.name like ?", getName(), condition, values);
        addConditionIfAnyValue("o.userId like ?", getUserId(), condition, values);
        addConditionIfAnyValue("o.description like ?", getDescription(), condition, values);
        addConditionIfAnyValue("o.department.id = ?", getDepartment() != null ? getDepartment().getId() : null, condition, values);
        addConditionIfAnyValue("o.id = ?", getId(), condition, values);

        if (getGroup() != null) {
            addConditionIfAnyValue("o.group.id = ?", getGroup().getId(), condition, values);
        }

        if (!values.isEmpty()) {
            sb.append(" where ");
            sb.append(join(condition, " and "));
        }

        return sb.toString() + " order by o.name";
    }

    private void addConditionIfAnyValue(String jpql, Object value, List<Object> sb, List<Object> values) {
        if (!isBlank(value)) {
            sb.add(jpql);
            if (value instanceof String){
                values.add("%" + value + "%");
            } else {
                values.add(value);
            }
        }
    }

    private boolean isBlank(Object s) {
        if (s == null) {
            return true;
        }
        if (s instanceof String){
            return "".equals(((String)s).trim());
        }
        return false;
    }

    /**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param list    the different strings to be concatenated
     * @return as string product of the parameters.
     */
    public static String join(List<?> list, String junctor) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0) + "";
        }

        for (int i = 0, j = list.size() - 1; i < j; i++) {
            sb.append(list.get(i));
            sb.append(junctor);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }

    public String getIdAsText() {
        if (idAsText == null  && id != null) {
            return id.toString();
        }
        return idAsText;
    }

    public void setIdAsText(String idAsText) {
        if ("".equals(idAsText)) {
            setId(null);
        }
        Long id = null;
        try {
            id = Long.parseLong(idAsText);
            setId(id);
            this.idAsText = idAsText;
        } catch (Exception e) {
            System.out.println(idAsText + " is not a long value.");
        }
    }
}
