package se.vgregion.ifeed.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import net.sf.cglib.beans.BeanMap;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_fields_inf")
public class FieldsInf extends AbstractEntity<Long> implements Serializable, Comparable<FieldsInf> {

    private static final long serialVersionUID = 1L;

    public FieldsInf() {
        // For the jpa.
    }

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    private String creatorId;

    @Column(length = 100000)
    private String text;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<FieldInf> getFieldInfs() {
        List<FieldInf> result = new ArrayList<FieldInf>();

        String[] rows = getText().split(Pattern.quote("\n"));

        Map<Integer, String> fieldPosition = new HashMap<Integer, String>();
        String[] first = rows[0].split(Pattern.quote("|"));

        for (int i = 0; i < first.length; i++) {
            fieldPosition.put(i, first[i].trim());
        }

        List<FieldInf> nestedResult = result;

        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            String[] cells = row.split(Pattern.quote("|"));
            FieldInf fi = new FieldInf();

            BeanMap bm = BeanMap.create(fi);
            for (int c = 0, j = fieldPosition.size(); c < j; c++) {
                if (c >= cells.length) {
                    continue;
                }
                String name = fieldPosition.get(c);
                Class<?> type = bm.getPropertyType(name);

                if (type.equals(Boolean.TYPE)) {
                    String part = cells[c].trim();
                    boolean b = "yes".equalsIgnoreCase(part);
                    bm.put(name, b);
                } else if (type.equals(List.class) && name.equals("children")) {
                    ((List) bm.get(name)).add(cells[c].trim());
                } else {
                    bm.put(name, cells[c].trim());
                }
            }

            if (fi.isLabel()) {
                nestedResult = fi.getChildren();
                result.add(fi);
            } else {
                fi.setId(fi.getId().toLowerCase());
                nestedResult.add(fi);
            }
        }

        return result;
    }

    public void putFieldInfInto(IFeedFilter item) {
        item.setFieldInf(getInfByFilterKey(item.getFilterKey()));
    }

    public void putFieldInfInto(Iterable<IFeedFilter> items) {
        for (IFeedFilter item : items) {
            putFieldInfInto(item);
        }
    }

    FieldInf getInfByFilterKey(String id) {
        return getInfByFilterKey(getFieldInfs(), id);
    }

    FieldInf getInfByFilterKey(List<FieldInf> infs, String id) {
        for (FieldInf fi : infs) {
            if (fi.getId().equalsIgnoreCase(id)) {
                return fi;
            }
            FieldInf result = getInfByFilterKey(fi.getChildren(), id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public int compareTo(FieldsInf o) {
        return (int) (id - o.id);
    }

}
