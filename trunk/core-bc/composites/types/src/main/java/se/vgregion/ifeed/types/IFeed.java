package se.vgregion.ifeed.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_ifeed")
public class IFeed extends AbstractEntity<Long> implements Serializable {
    private static final long serialVersionUID = -2277251806545192506L;

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeed.class);

    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Long version;

    @ElementCollection
    @CollectionTable(name = "vgr_ifeed_filter", joinColumns = @JoinColumn(name = "ifeed_id"))
    private Set<IFeedFilter> filters;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();
    private String description;
    private Long userId;

    public Collection<IFeedFilter> getFilters() {
        if(filters == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<IFeedFilter>(filters));
    }

    public boolean addFilter(IFeedFilter filter) {
        return filters.add(filter);
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
        return new Date(timestamp.getTime());
    }

    public void setTimestamp(Date timestamp) {
        if(timestamp == null) {
            setTimestamp();
        } else {
            this.timestamp = new Date(timestamp.getTime());
        }
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

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }
}
