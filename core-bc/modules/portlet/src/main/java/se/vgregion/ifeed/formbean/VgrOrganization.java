package se.vgregion.ifeed.formbean;

import se.vgregion.ifeed.el.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VgrOrganization extends BaseVgrOrganization {

    private boolean open;

    private boolean added;

    private int level;

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

    private List<VgrOrganization> children = new ArrayList<VgrOrganization>();

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

    public List<VgrOrganization> getChildren() {
        return children;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
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