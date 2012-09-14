package se.vgregion.ifeed.formbean;

import java.util.regex.Pattern;

public class VgrOrganization extends BaseVgrOrganization {

    public VgrOrganization() {
        super();
    }

    public VgrOrganization(String dn, String hsaIdentity) {
        super(dn, hsaIdentity);
    }

    public String getLabel() {
        if (getOu() != null) {
            return getOu();
        }
        return getCn();
    }

    public String getId() {
        String result = getDn();
        if (result != null) {
            result = result.replaceAll(Pattern.quote(","), " ");
        }
        return result;
    }

    public String getQuery() {
        return getHsaIdentity(); // + "|" + getLabel();
    }

}