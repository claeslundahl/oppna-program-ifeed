package se.vgregion.ifeed.types;


import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-24.
 */
@Entity
@Table(name = "vgr_ifeed_department")
public class VgrDepartment extends AbstractEntity<Long> implements Serializable, Comparable<VgrDepartment> {

    @Id
    @GeneratedValue
    protected Long id;

    private String name;

    private String description;

    @Transient
    private Boolean open;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "department")
    private List<VgrGroup> vgrGroups = new ArrayList<VgrGroup>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<IFeed> memberFeeds = new ArrayList<IFeed>();

    @Override
    public int compareTo(VgrDepartment o) {
        if (o == null) {
            return 1;
        }
        return hashCode() - o.hashCode();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<VgrGroup> getVgrGroups() {
        return vgrGroups;
    }

    public void setVgrGroups(List<VgrGroup> vgrGroups) {
        this.vgrGroups = vgrGroups;
    }


    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public List<IFeed> getMemberFeeds() {
        return memberFeeds;
    }

    public void setMemberFeeds(List<IFeed> memberFeeds) {
        this.memberFeeds = memberFeeds;
    }
}
