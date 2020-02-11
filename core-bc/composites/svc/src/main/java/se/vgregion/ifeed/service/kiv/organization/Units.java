
package se.vgregion.ifeed.service.kiv.organization;

import java.util.List;
// import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

// @Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Units {

    @SerializedName("meta")
    private Meta mMeta;
    @SerializedName("units")
    private List<Unit> mUnits;

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
    }

    public List<Unit> getUnits() {
        return mUnits;
    }

    public void setUnits(List<Unit> units) {
        mUnits = units;
    }

}
