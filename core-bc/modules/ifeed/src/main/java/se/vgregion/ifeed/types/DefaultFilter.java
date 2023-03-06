package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "default_filter")
public class DefaultFilter {

    private static final long serialVersionUID = 8141707337621433678L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String filterQuery;

    @Column
    private String filterKey;

    public Filter toFilter() {
        Filter result = new Filter();
        result.setId(id * -1);
        result.setFilterKey(filterKey);
        result.setFilterQuery(filterQuery);
        // result.setId(new Long(filterKey != null ? filterKey.hashCode() : 0 + filterQuery != null ? filterQuery.hashCode() : 0));
        return result;
    }

}
