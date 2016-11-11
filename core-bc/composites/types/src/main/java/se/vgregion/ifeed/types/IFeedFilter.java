package se.vgregion.ifeed.types;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.lang.builder.ToStringBuilder;

import se.vgregion.dao.domain.patterns.valueobject.AbstractValueObject;

@Embeddable
public final class IFeedFilter extends AbstractValueObject implements Serializable {

	private static final long serialVersionUID = -8141707337621433677L;

    @Column(nullable = false)
    private String filterQuery;

	private String filterKey;

    private String operator = "matching"; // matching | greater | lesser

    @Transient
    private FieldInf fieldInf;

    @Transient
    private Object filterQueryForDisplay;

    @Deprecated
	@Enumerated(EnumType.STRING)
	private FilterType.Filter filter;

	public IFeedFilter() {
		// To make Hibernate happy
	}

    public IFeedFilter(FilterType.Filter filter, String filterQuery, String filterKey) {
        this.filter = filter;
        this.filterQuery = filterQuery;
        this.filterKey = filterKey;
    }

    public IFeedFilter(String filterQuery, String filterKey) {
        this.filterQuery = filterQuery;
        this.filterKey = filterKey;
    }

	public FilterType.Filter getFilter() {
		return filter;
	}

	public String getFilterQuery() {
		return filterQuery;
	}

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    @Override
	public String toString() {
		return new ToStringBuilder(this).append(filter).append(filterQuery).toString();
	}

	public String getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(String filterKey) {
		this.filterKey = filterKey;
	}

    public FieldInf getFieldInf() {
        return fieldInf;
    }

    public void setFieldInf(FieldInf fieldInf) {
        this.fieldInf = fieldInf;
    }

    public Object getFilterQueryForDisplay() {
        return filterQueryForDisplay;
    }

    public void setFilterQueryForDisplay(Object filterQueryForDisplay) {
        this.filterQueryForDisplay = filterQueryForDisplay;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
