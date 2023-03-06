package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-08-21.
 */
@Entity
@Data
@Table(name = "vgr_ifeed_dynamic_table")
public class DynamicTableDef {

    @Id
    @GeneratedValue
    protected Long id;
    private String name;

    private String fontSize;
    private String defaultSortColumn;
    private String defaultSortOrder;
    private String feedId;
    private String feedHome;

    @Column(name = "max_hit_limit")
    private Integer maxHitLimit = 0;

    private Boolean showTableHeader = true;
    private Boolean linkOriginalDoc = false;
    private Boolean hideRightColumn = false;

    @OneToMany(mappedBy = "tableDef", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();

    @OneToMany(mappedBy = "tableDef", cascade = CascadeType.ALL)
    // @OrderBy("name index")
    private List<DynamicTableSortingDef> extraSorting = new ArrayList<DynamicTableSortingDef>();

    @ManyToOne
    @JoinColumn(name = "fk_ifeed_id")
    // @Expose(serialize = false, deserialize = false)
    private Feed ifeed;

    @Column(name = "fk_ifeed_id", updatable = false, insertable = false)
    private Long fkIfeedId;

}
