/**
 *
 */
package se.vgregion.ifeed.types;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.CompareToBuilder;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Anders Asplund - Callista Enterprise
 *
 */
@Entity
@Table(name = "vgr_apelon_metadata")
public class Metadata extends AbstractEntity<Long>
        implements Comparable<Metadata> {

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
    @Expose(serialize = false, deserialize = false)
    private Set<FieldInf> fieldInfs = new HashSet<>();

    public Metadata() {
        // To make JPA/Hibernate Happy
    }

    public Metadata(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Collection<Metadata> getChildren() {
        return children;
    }

    public void addChild(Metadata child) {
        children.add(child);
    }

    public void removeAllChildren() {
        children.clear();
    }

    @Override
    public int compareTo(Metadata other) {
        return new CompareToBuilder().
                append(this.name, other.name).toComparison();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public Set<FieldInf> getFieldInfs() {
        return fieldInfs;
    }

    public void setFieldInfs(Set<FieldInf> fieldInfs) {
        this.fieldInfs = fieldInfs;
    }
}
