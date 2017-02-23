package se.vgregion.ifeed.shared;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds description of a column. How it should be rendered to the user.
 * <code>
 * create table vgr_ifeed_dynamic_column_def (
 * id bigint not null,
 * table_def_id bigint,
 * name character varying(255),
 * label character varying(255),
 * alignment character varying(255),
 * width character varying(255),
 * CONSTRAINT vgr_ifeed_dynamic_column_def_pkey PRIMARY KEY (id),
 * CONSTRAINT vgr_ifeed_dynamic_column_def_2_vgr_ifeed_dynamic_table_def FOREIGN KEY (table_def_id)
 * REFERENCES vgr_ifeed_dynamic_table_def (id) MATCH SIMPLE
 * );
 * </code>
 */
@Entity
@Table(name = "vgr_ifeed_dynamic_column_def")
public class ColumnDef extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_table_def_id")
    private DynamicTableDef tableDef;

    private String name;
    private String label;
    private String alignment;
    private String width = "70";

    /**
     * Takes a text that contains information about the column in question. Then it parses that string and puts
     * appropriate values inside its own properties based on that.
     *
     * @param values the text describing the object. An example woud be "dc.title|Titel|left|70".
     */
    public void parseAndSet(String values) {
        String[] fragments = values.split("['|']");
        setName(fragments[0]);
        setLabel(fragments[1]);
        setAlignment(fragments[2]);
        if (fragments.length > 3) setWidth(fragments[3]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        System.out.println("asdf " + name);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toStringImpl() {
        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("tableDef", tableDef);
        map.put("name", name);
        map.put("width", width);
        map.put("id", id);
        map.put("label", label);
        map.put("alignment", alignment);
        map.put("class", getClass());
        return map.toString();
    }

    public void setTableDef(DynamicTableDef tableDef) {
        this.tableDef = tableDef;
    }
}
