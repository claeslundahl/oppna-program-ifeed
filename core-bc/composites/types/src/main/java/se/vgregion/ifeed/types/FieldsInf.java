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
@Table(name = "fields_inf")
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

        String t = getText();
        String[] rows = t.split(Pattern.quote("\n"));

        Map<Integer, String> fieldPosition = new HashMap<Integer, String>();
        String[] first = rows[0].split(Pattern.quote("|"));

        for (int i = 0; i < first.length; i++) {
            fieldPosition.put(i, first[i].trim());
        }
        // System.out.println("fieldPosition: " + fieldPosition);

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

                if (type.isPrimitive()) {
                    bm.put(name, "yes".equalsIgnoreCase(cells[c].trim()));
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

    @Override
    public int compareTo(FieldsInf o) {
        return (int) (id - o.id);
    }

}
