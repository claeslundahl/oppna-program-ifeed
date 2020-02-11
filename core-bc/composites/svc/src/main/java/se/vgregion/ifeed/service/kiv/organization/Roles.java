
package se.vgregion.ifeed.service.kiv.organization;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Roles {

    @SerializedName("meta")
    private Meta mMeta;
    @SerializedName("roles")
    private List<Unit> mRoles;

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
    }

    public List<Unit> getRoles() {
        return mRoles;
    }

    public void setRoles(List<Unit> roles) {
        mRoles = roles;
    }

}
