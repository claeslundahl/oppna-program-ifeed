package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Deprecated // Is this used?
@Entity
@Data
@Table(name = "vgr_ifeed_config")
public class Config {

    private static final long serialVersionUID = 98098L;

    public Config() {
        // For the jpa.
    }

    @Id
    private String id;

    @Column(length = 200_000)
    private String setting;

}
