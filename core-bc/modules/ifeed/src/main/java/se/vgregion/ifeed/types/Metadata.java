/**
 *
 */
package se.vgregion.ifeed.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Anders Asplund - Callista Enterprise
 */
@Entity
@Data
@Table(name = "vgr_apelon_metadata")
public class Metadata {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String key;

    @Column
    private String name;

    @Column(name = "filter_query")
    private String filterQuery;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Collection<Metadata> children = new HashSet<Metadata>();

    @OneToMany(mappedBy = "metadata", fetch = FetchType.LAZY, orphanRemoval = false)
    // @Expose(serialize = false, deserialize = false)
    @JsonIgnore
    private Set<FieldInf> fieldInfs = new HashSet<>();

    @Column(name = "active")
    private Boolean active;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "metadata")
    @JsonIgnore
    private List<Filter> filters;

    public Metadata() {
        // To make JPA/Hibernate Happy
    }

    public Metadata(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", filterQuery='" + filterQuery + '\'' +
                ", active=" + active +
                '}';
    }
}
