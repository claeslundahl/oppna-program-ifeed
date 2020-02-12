
package se.vgregion.ifeed.service.kiv.organization;

import java.util.List;
// import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

// @Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Units {

    @SerializedName("meta")
    private Meta meta;
    @SerializedName("units")
    private List<Unit> units;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

}
