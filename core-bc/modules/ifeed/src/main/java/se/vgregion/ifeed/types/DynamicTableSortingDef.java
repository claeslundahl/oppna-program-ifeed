package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "vgr_ifeed_dynamic_table_sorting")
public class DynamicTableSortingDef {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_table_def_id")
    // @Expose(serialize = false, deserialize = false)
    private DynamicTableDef tableDef;

    private String name;

    private Integer index;

    private String direction;

}
