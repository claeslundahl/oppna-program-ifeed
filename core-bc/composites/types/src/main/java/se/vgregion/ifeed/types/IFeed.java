package se.vgregion.ifeed.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_ifeed")
public class IFeed extends AbstractEntity<Long> implements Serializable, Comparable<IFeed> {

    private static final long serialVersionUID = -2277251806545192506L;
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeed.class);

    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Long version;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vgr_ifeed_filter", joinColumns = @JoinColumn(name = "ifeed_id"))
    private List<IFeedFilter> filters;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = null;
    private String description;
    private String userId;
    private String sortField;
    private String sortDirection;

    public List<IFeedFilter> getFilters() {
        if (filters == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(filters);
    }

    public boolean addFilter(IFeedFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        if (filters == null) {
            filters = new ArrayList<IFeedFilter>();
        } else if (filters.contains(filter)) {
            return false;
        }
        filters.add(filter);
        return true;
    }

    public IFeedFilter getFilter(IFeedFilter filter) {
        IFeedFilter f = null;
        int index = filters.indexOf(filter);
        if (index >= 0) {
            f = filters.get(index);
        }
        return f;
    }

    public void setFilters(List<IFeedFilter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
    }

    public void removeFilter(IFeedFilter filter) {
        filters.remove(filter);
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

}
