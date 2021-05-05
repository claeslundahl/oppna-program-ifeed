package se.vgregion.ifeed.types;

import javax.persistence.*;

@Entity
@Table(name = "default_filter")
public class DefaultFilter {

    private static final long serialVersionUID = 8141707337621433678L;

    @Id
    @GeneratedValue
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Column
    private String filterQuery;

    @Column
    private String filterKey;

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public IFeedFilter toFilter() {
        IFeedFilter result = new IFeedFilter();
        result.setFilterKey(filterKey);
        result.setFilterQuery(filterQuery);
        result.setId(new Long(filterKey != null ? filterKey.hashCode() : 0 + filterQuery != null ? filterQuery.hashCode() : 0));
        return result;
    }

}
