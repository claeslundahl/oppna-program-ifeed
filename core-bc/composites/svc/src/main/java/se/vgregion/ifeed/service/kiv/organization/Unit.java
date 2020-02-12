
package se.vgregion.ifeed.service.kiv.organization;

// import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

// @Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Unit {

    @SerializedName("attributes")
    private Attributes attributes;
    @SerializedName("dn")
    private String dn;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

}
