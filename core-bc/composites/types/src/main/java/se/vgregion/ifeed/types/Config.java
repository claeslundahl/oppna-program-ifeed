package se.vgregion.ifeed.types;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vgr_ifeed_config")
public class Config extends AbstractEntity<String> implements Serializable, Comparable<Config> {

    private static final long serialVersionUID = 98098L;

    public Config() {
        // For the jpa.
    }

    @Id
    private String id;

    @Column(length = 200_000)
    private String setting;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int compareTo(Config o) {
        if (o == null) {
            return 1;
        }
        return id.compareTo(o.id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String settings) {
        this.setting = settings;
    }

}
