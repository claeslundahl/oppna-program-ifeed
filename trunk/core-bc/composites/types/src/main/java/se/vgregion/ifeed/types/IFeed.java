package se.vgregion.ifeed.types;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_ifeed")
public class IFeed extends AbstractEntity<Long> implements
		Serializable {
	private static final long serialVersionUID = -2277251806545192506L;

	@Id
	@GeneratedValue
	private Long id;
	@Version
	private Long version;

	@ElementCollection
	@CollectionTable(name = "vgr_ifeed_filter",
			         joinColumns=@JoinColumn(name="ifeed_id"))
	private List<IFeedFilter> filters;
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	private String description;
	private Long userId;

	public List<IFeedFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<IFeedFilter> filters) {
		this.filters = filters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
