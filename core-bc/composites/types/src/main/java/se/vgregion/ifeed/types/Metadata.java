/**
 * 
 */
package se.vgregion.ifeed.types;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

/**
 * @author Anders Asplund - Callista Enterprise
 *
 */
@Entity
@Table(name="vgr_apelon_metadata")
public class Metadata extends AbstractEntity<Long> implements Comparable<Metadata> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private Collection<Metadata> children = new HashSet<Metadata>();

    Metadata() {
        //To make JPA/Hibernate Happy
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
        return new CompareToBuilder().append(this.name, other.name).toComparison();
    }
}
