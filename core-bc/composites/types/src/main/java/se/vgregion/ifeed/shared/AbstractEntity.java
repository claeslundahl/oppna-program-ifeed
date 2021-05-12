package se.vgregion.ifeed.shared;

import org.hibernate.LazyInitializationException;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by clalu4 on 2015-04-09.
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Transient
    private boolean toStringRunning;

    @Override
    public String toString() {
        try {
            if (toStringRunning) {
                toStringRunning = true;
                return "{Recursive toString for type " + getClass().getSimpleName() + ", hasCode " + hashCode() + "}";
            } else {
                return toStringImpl();
            }
        }catch (Exception lie) {
            return lie.getClass().getSimpleName();
        } finally {
            toStringRunning = false;
        }
    }

    protected abstract String toStringImpl();

    public abstract Long getId();

    public abstract void setId(Long id);

    @Override
    public int hashCode() {
        if (getId() == null) {
            return -1;
        } else {
            return getId().intValue();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }
        try {
            AbstractEntity ae = (AbstractEntity) obj;
            if (ae.getId() == getId()) {
                return true;
            }
            if (ae.getId() == null || getId() == null) {
                return false;
            }
            return getId().equals(ae.getId());
        } catch (ClassCastException cce) {
            return false;
        }
    }

}
