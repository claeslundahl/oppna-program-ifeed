package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "field_inf")
public class FieldInf implements Serializable {

    public FieldInf() {
        super();
    }

    public FieldInf(List<FieldInf> children) {
        this();
        this.children.addAll(children);
    }

    @Id
    @GeneratedValue
    private Long pk;
    @Column(name = "level")
    Integer level;
    @Column(name = "in_tooltip")
    Boolean inTooltip = false;
    @Column(name = "apelon_key")
    String apelonKey;
    @Column(name = "type")
    String type;
    @Column(name = "operator")
    String operator;
    @Column(name = "query_prefix")
    private String queryPrefix;
    @Column(name = "filter")
    Boolean filter = false;
    @Column(name = "help")
    String help;
    @Column(name = "expanded")
    Boolean expanded = false;
    @Column(name = "name")
    String name;
    @Column(name = "id")
    String id;
    @Column(name = "in_html_view")
    Boolean inHtmlView = false;
    @Column(name = "value")
    String value;
    @Column(name = "position")
    private Integer position;
    @Column(name = "parent_pk", insertable = false, updatable = false)
    private Long parentPk;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_pk")
    // @Expose(serialize = false, deserialize = true)
    private FieldInf parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("position")
    private List<FieldInf> children = new ArrayList<>();

    @CollectionTable(name = "field_counterpart", joinColumns = @JoinColumn(name = "field_inf_pk"))
    @ElementCollection(fetch = FetchType.EAGER) // TODO: How to set the name of the column?
    @Column(name = "field_inf_id")
    private Set<String> counterparts;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_inf_pk")
    private Set<DefaultFilter> defaultFilters;

    @OneToMany(mappedBy = "fieldInf", orphanRemoval = false)
    // @Expose(serialize = false)
    private Set<Filter> filters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apelon_id")
    // // @Expose(serialize = false, deserialize = false)
    private Metadata metadata;

}
