package se.vgregion.ifeed.formbean;

import se.vgregion.ifeed.el.Json;

import java.util.regex.Pattern;

public class VgrOrganization extends BaseVgrOrganization {

    public VgrOrganization() {
        super();
        id = new Id(this);
    }

    public VgrOrganization(String dn, String hsaIdentity) {
        super(dn, hsaIdentity);
    }

    private Id id;

    public String getLabel() {
        if (getOu() != null) {
            return getOu();
        }
        return getCn();
    }

    /*
    public String getId() {
        String result = getDn();
        if (result != null) {
            result = result.replaceAll(Pattern.quote(","), " ");
        }
        if (getLabel() != null) {
            result += "LABEL" + getLabel();
        }
        return result;
    }*/

    public String getQuery() {
        return getHsaIdentity(); // + "|" + getLabel();
    }

    public String getId() {
        return Json.toJson(id);
    }

    public static class Id extends BaseVgrOrganization {

        private VgrOrganization parent;

        public Id(VgrOrganization parent) {
            this.parent = parent;
        }

        public String getLabel() {
            return parent.getLabel();
        }

        @Override
        public String getHsaIdentity() {
            return parent.getHsaIdentity();
        }

    }

}