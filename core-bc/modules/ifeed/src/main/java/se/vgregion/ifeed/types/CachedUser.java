package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Patrik Björk
 */
@Entity
@Data
@Table(name = "vgr_ifeed_cached_user")
public class CachedUser implements Serializable {

    @Id
    private String id;

    @Column
    private String passwordHash;

    @Column
    private String dn;

    @Column
    private String mail;

    @Column
    private String displayName;

    @Column
    private Boolean admin;

}
