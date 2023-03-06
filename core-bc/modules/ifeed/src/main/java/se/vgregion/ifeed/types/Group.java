package se.vgregion.ifeed.types;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-24.
 */
@Entity
@Table(name = "vgr_ifeed_group")
public class Group {

    @Id
    @GeneratedValue
    protected Long id;

    private String name;

    private String description;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    // @Expose(serialize = false, deserialize = false)
    private Department department;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Feed> memberFeeds = new ArrayList<Feed>();

}
