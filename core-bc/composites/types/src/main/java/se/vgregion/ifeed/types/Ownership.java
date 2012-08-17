package se.vgregion.ifeed.types;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

//@Embeddable
//public final class Ownership extends AbstractValueObject implements Serializable, Comparable<Ownership> {

@Entity
@Table(name = "vgr_ifeed_ownership")
public class Ownership extends AbstractEntity<Long> implements Serializable, Comparable<Ownership> {

	@Id
	@GeneratedValue
	private Long id;

	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "ifeed_id", updatable = false, insertable = false)
	private Long ifeedId;

	@Transient
	private String name;

	@ManyToOne
	@JoinColumn(name = "ifeed_id", nullable = false)
	private IFeed ifeed;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Ownership another) {
		return another.getUserId().compareTo(getUserId());
	}

	public String getUserId() {
		if (userId == null) {
			return "";
		}
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getIfeedId() {
		return ifeedId;
	}

	public void setIfeedId(Long ifeedId) {
		this.ifeedId = ifeedId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
