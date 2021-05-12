package se.vgregion.ifeed.types;

import se.vgregion.common.utils.BeanMap;
import se.vgregion.ifeed.shared.AbstractEntity;

import javax.persistence.*;
import java.util.TreeMap;

@Entity
@Table(name = "default_filter")
public class DefaultFilter extends AbstractEntity {

    private static final long serialVersionUID = 8141707337621433678L;

    @Id
    @GeneratedValue
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    protected String toStringImpl() {
        return new TreeMap(new BeanMap(this)).toString();
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
        result.setId(id * -1);
        result.setFilterKey(filterKey);
        result.setFilterQuery(filterQuery);
        // result.setId(new Long(filterKey != null ? filterKey.hashCode() : 0 + filterQuery != null ? filterQuery.hashCode() : 0));
        return result;
    }

}
