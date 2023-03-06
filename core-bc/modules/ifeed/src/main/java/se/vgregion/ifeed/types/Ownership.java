package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;

//@Embeddable
//public final class Ownership extends AbstractValueObject implements Serializable, Comparable<Ownership> {

@Entity
@Data
@Table(name = "vgr_ifeed_ownership")
public class Ownership {

    @Id
    @GeneratedValue
    private Long id;

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "ifeed_id", updatable = false, insertable = false)
    private Long ifeedId;

    @Transient
    private String name;

    @ManyToOne
    @JoinColumn(name = "ifeed_id", nullable = false)
    // @Expose(serialize = false, deserialize = true)
    private Feed ifeed;

}
