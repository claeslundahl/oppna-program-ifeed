
package se.vgregion.ifeed.service.kiv.organization;

// import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

// @Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Unit {

    @SerializedName("attributes")
    private Attributes mAttributes;
    @SerializedName("dn")
    private String mDn;

    public Attributes getAttributes() {
        return mAttributes;
    }

    public void setAttributes(Attributes attributes) {
        mAttributes = attributes;
    }

    public String getDn() {
        return mDn;
    }

    public void setDn(String dn) {
        mDn = dn;
    }

}
