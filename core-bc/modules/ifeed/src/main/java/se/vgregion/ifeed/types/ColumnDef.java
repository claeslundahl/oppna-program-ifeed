package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;

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
@Data
@Table(name = "vgr_ifeed_dynamic_column_def")
public class ColumnDef {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_table_def_id")
    // @Expose(serialize = false, deserialize = true)
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


}
