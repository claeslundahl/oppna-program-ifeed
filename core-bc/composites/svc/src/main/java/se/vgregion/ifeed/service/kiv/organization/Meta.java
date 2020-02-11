
package se.vgregion.ifeed.service.kiv.organization;

// import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

// @Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Meta {

    @SerializedName("Create time")
    private String mCreateTime;
    @SerializedName("Version")
    private String mVersion;

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String createTime) {
        mCreateTime = createTime;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

}
