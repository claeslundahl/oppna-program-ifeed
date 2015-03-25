package se.vgregion.ifeed.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.builder.CompareToBuilder;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

@Entity
@Table(name = "vgr_ifeed")
public class IFeed extends AbstractEntity<Long> implements Serializable, Comparable<IFeed> {

    private static final long serialVersionUID = -2277251806545192506L;
    // private static final Logger LOGGER = LoggerFactory.getLogger(IFeed.class);

    @Id
    @GeneratedValue
    protected Long id;

    @Version
    private Long version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vgr_ifeed_filter", joinColumns = @JoinColumn(name = "ifeed_id"))
    // @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
    protected
    Set<IFeedFilter> filters;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = null;

    private String description;
    private String userId;

    @Transient
    private String creatorName;

    @ManyToOne
    private VgrDepartment department;

    @ManyToOne
    private VgrGroup group;

    // @ElementCollection(fetch = FetchType.EAGER)
    // @CollectionTable(name = "vgr_ifeed_ownership", joinColumns = @JoinColumn(name = "ifeed_id"))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
    protected Set<Ownership> ownerships = new HashSet<Ownership>();

    private String sortField;
    private String sortDirection;

    private Boolean linkNativeDocument;

    public Set<IFeedFilter> getFilters() {
        if (filters == null) {
            return Collections.emptySet();
        }
        //return Collections.unmodifiableSet(filters);
        return filters;
    }

    public boolean addFilter(IFeedFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        if (filters == null) {
            filters = new HashSet<IFeedFilter>();
        } else if (filters.contains(filter)) {
            return false;
        }
        filters.add(filter);
        return true;
    }

    public IFeedFilter getFilter(IFeedFilter filter) {
        IFeedFilter f = null;
        List<IFeedFilter> filters = new ArrayList<IFeedFilter>(getFilters());
        int index = filters.indexOf(filter);
        if (index >= 0) {
            f = filters.get(index);
        }
        return f;
    }

    public void setFilters(Set<IFeedFilter> filters) {
        if (this.filters == null) {
            this.filters = new HashSet<IFeedFilter>();
        }
        this.filters.clear();
        this.filters.addAll(filters);
    }

    public void removeFilter(IFeedFilter filter) {
        filters.remove(filter);
    }

    public void removeFilter(int index) {
        List<IFeedFilter> temp = new ArrayList<IFeedFilter>(filters);
        temp.remove(index);
        filters.retainAll(temp);
    }

    public void removeFilters() {
        filters.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        if (timestamp == null) {
            return null;
        } else {
            return new Date(timestamp.getTime());
        }
    }

    public void clearTimestamp() {
        this.timestamp = null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp() {
        this.timestamp = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public final int compareTo(final IFeed o) {
        if (o == null) {
            return +1;
        }
        return new CompareToBuilder().append(name, o.name).toComparison();
    }

    public Set<Ownership> getOwnerships() {
        return ownerships;
    }

    private void setOwnerships(Set<Ownership> ownerships) {
        this.ownerships = ownerships;
    }

    public String getOwnershipsText() {
        List<String> names = new ArrayList<String>();
        for (Ownership ownership : getOwnerships()) {
            names.add(ownership.getUserId());
        }
        names.remove(getUserId());
        names.add(0, getUserId());
        String text = names.toString();
        text = text.replaceAll(Pattern.quote("["), "").replaceAll(Pattern.quote("]"), "");
        return text;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Boolean getLinkNativeDocument() {
        return linkNativeDocument;
    }

    public void setLinkNativeDocument(Boolean linkNativeDocument) {
        this.linkNativeDocument = linkNativeDocument;
    }

    public VgrDepartment getDepartment() {
        return department;
    }

    public void setDepartment(VgrDepartment department) {
        this.department = department;
    }

    public VgrGroup getGroup() {
        return group;
    }

    public void setGroup(VgrGroup group) {
        this.group = group;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static IFeed fromJson(String ifeed) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(ifeed, IFeed.class);
    }

}
