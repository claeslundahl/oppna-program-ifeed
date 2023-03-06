package se.vgregion.ifeed.types;


import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-24.
 */
@Entity
@Data
@Table(name = "vgr_ifeed_department")
public class Department {

    @Id
    @GeneratedValue
    protected Long id;

    private String name;

    private String description;

    @Transient
    private Boolean open;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "department")
    private List<Group> groups = new ArrayList<Group>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Feed> memberFeeds = new ArrayList<Feed>();

}
