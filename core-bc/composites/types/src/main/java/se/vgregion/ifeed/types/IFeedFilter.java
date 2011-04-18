package se.vgregion.ifeed.types;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;

import se.vgregion.dao.domain.patterns.valueobject.AbstractValueObject;


@Embeddable
public class IFeedFilter extends AbstractValueObject implements Serializable {

	private static final long serialVersionUID = -8141707337621433677L;

	public enum FilterType { CREATED_BY, CREATED_FOR, MANAGED_BY, KEYWORDS, STATUS_AND_LIMITATIONS, CONTEXT, HSA_CONTEXT }	
	
	@GeneratedValue
	private Long id;
	private String filterQuery;
	private String filterType;
	
	
	public String getFilterType() {
		return filterType;
	}
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFilterQuery() {
		return filterQuery;
	}
	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}
}
