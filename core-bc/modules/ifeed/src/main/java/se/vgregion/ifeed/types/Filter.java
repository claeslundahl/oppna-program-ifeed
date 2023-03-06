package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "vgr_ifeed_filter")
public class Filter {

    @Id
    @GeneratedValue
    private Long id;

    private static final long serialVersionUID = -8141707337621433677L;

    @Column
    private String filterQuery;

    @Column
    private String filterKey;

    @Column
    private String operator = "matching";

    @Column(name = "using_and")
    private Boolean usingAnd = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_inf_pk")
    private FieldInf fieldInf;

    @Transient
    private Object filterQueryForDisplay;

  /*  @Deprecated
    @Enumerated(EnumType.STRING)
    private FilterType.Filter filter;
*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ifeed_id")
    /*@ForeignKey(name = "fk_ifeed_filter_ifeed")*/
    // @Expose(serialize = false, deserialize = true)
    private Feed feed;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    /*@ForeignKey(name = "fk_ifeed_filter_parent")*/
    // @Expose(serialize = false, deserialize = true)
    private Filter parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apelon_id")
    // @Expose(serialize = false, deserialize = false)
    private Metadata metadata;

    @OneToMany(mappedBy = "parent", targetEntity = Filter.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Filter> children = new ArrayList<>();

/*    public IFeedFilter(FilterType.Filter filter, String filterQuery, String filterKey) {
        this.filter = filter;
        this.filterQuery = filterQuery;
        this.filterKey = filterKey;
    }*/

    public Filter(String filterQuery, String filterKey) {
        this.filterQuery = filterQuery;
        this.filterKey = filterKey;
    }

    public Filter() {

    }

}
