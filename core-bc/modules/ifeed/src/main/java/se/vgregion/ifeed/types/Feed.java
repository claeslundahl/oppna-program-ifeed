package se.vgregion.ifeed.types;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import se.vgregion.ifeed.types.using.DistinctArrayList;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@Table(name = "vgr_ifeed")
public class Feed {

    private static final long serialVersionUID = -2277251806545192506L;

    @Id
    @GeneratedValue
    protected Long id;

    @OneToMany(mappedBy = "ifeed", cascade = CascadeType.ALL)
    private final List<DynamicTableDef> dynamicTableDefs = new ArrayList<DynamicTableDef>();

    @Version
    @Column
    private Long version;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "feed")
    protected Set<Filter> filters = new HashSet<>();

    @Column
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date timestamp = null;

    @Column
    private String description;

    @Column
    private String userId;

    @Transient
    private String creatorName;

    @ManyToOne
    // @Expose(serialize = false, deserialize = true)
    private Department department;

    @ManyToOne
    // @Expose(serialize = false, deserialize = true)
    private Group group;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
    protected Set<Ownership> ownerships = new HashSet<>();

    @ManyToMany(/*cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    protected List<Feed> composites = new DistinctArrayList<>();

    // @Expose(serialize = false, deserialize = true)
    @ManyToMany(mappedBy = "composites"/*, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    protected List<Feed> partOf = new DistinctArrayList<>();

    @Column
    private String sortField;

    @Column
    private String sortDirection;

    @Column(name="linkNativeDocument")
    private Boolean linkNativeDocument;

    @Column(name = "creation_time", updatable = false)
    @CreationTimestamp
    private Date creationTime;

    @Column(name = "filter_change_lock", updatable = false, insertable = true, columnDefinition = "boolean default false")
    private Boolean filterChangeLock = false;

    /**
     * Message to user of ifeed-admin - to show it for the user if it has any value. As for now this value can only be
     * set by technical staff directly in the db.
     */
    @Column(name = "editor_user_message", length = 800, updatable = false, insertable = true)
    private String editorUserMessage;

}
