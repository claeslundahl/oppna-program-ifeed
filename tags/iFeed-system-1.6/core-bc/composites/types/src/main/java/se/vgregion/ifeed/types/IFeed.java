package se.vgregion.ifeed.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.CompareToBuilder;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_ifeed")
public class IFeed extends AbstractEntity<Long> implements Serializable, Comparable<IFeed> {

	private static final long serialVersionUID = -2277251806545192506L;
	// private static final Logger LOGGER = LoggerFactory.getLogger(IFeed.class);

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "vgr_ifeed_filter", joinColumns = @JoinColumn(name = "ifeed_id"))
	// @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
	private Set<IFeedFilter> filters;

	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp = null;

	private String description;
	private String userId;

	@Transient
	private String creatorName;

	// @ElementCollection(fetch = FetchType.EAGER)
	// @CollectionTable(name = "vgr_ifeed_ownership", joinColumns = @JoinColumn(name = "ifeed_id"))
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
	private Set<Ownership> ownerships = new HashSet<Ownership>();

	private String sortField;
	private String sortDirection;

	public Set<IFeedFilter> getFilters() {
		if (filters == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(filters);
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
		this.filters.clear();
		this.filters.addAll(filters);
	}

	public void removeFilter(IFeedFilter filter) {
		filters.remove(filter);
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

	public void setOwnerships(Set<Ownership> ownerships) {
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

}
