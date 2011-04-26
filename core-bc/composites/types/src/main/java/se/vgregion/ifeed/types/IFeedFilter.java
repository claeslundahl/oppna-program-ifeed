package se.vgregion.ifeed.types;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import se.vgregion.dao.domain.patterns.valueobject.AbstractValueObject;

@Embeddable
public final class IFeedFilter extends AbstractValueObject implements Serializable {

    private static final long serialVersionUID = -8141707337621433677L;

    @GeneratedValue
    private Long id;
    private String filterQuery;
    private FilterType.Filter filter;

    public IFeedFilter() {
        // To make Hibernate happy
    }

    public IFeedFilter(FilterType.Filter filter, String filterQuery) {
        this.filter = filter;
        this.filterQuery = filterQuery;
    }

    public FilterType.Filter getFilter() {
        return filter;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(filter).append(filterQuery).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append(filter).append(filterQuery).toString();
    }
}
