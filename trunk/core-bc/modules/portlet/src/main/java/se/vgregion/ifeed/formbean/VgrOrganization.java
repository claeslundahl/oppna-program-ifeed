package se.vgregion.ifeed.formbean;

import java.io.Serializable;

import se.vgregion.ldap.HasCommonLdapFields;

public class VgrOrganization implements Serializable, HasCommonLdapFields {

    // private static On onForAll = new On();

    private String dn;
    private String ou;
    private String io;
    private String type;
    // private On on = onForAll;
    private boolean leaf;

    @Override
    public String getDn() {
        return dn;
    }

    @Override
    public void setDn(String dn) {
        this.dn = dn;
    }

    @Override
    public String getOu() {
        return ou;
    }

    @Override
    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getLabel() {
        return ou;
    }

    public String getId() {
        return dn;
    }

    public String getIo() {
        return io;
    }

    public void setIo(String io) {
        this.io = io;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    // public On getOn() {
    // return on;
    // }
    //
    // public void setOn(On on) {
    // this.on = on;
    // }
    //
    // public static class On {
    // private String check = "defCallback", uncheck = "defCallback";
    //
    // public String getCheck() {
    // return check;
    // }
    //
    // public void setCheck(String check) {
    // this.check = check;
    // }
    //
    // public String getUncheck() {
    // return uncheck;
    // }
    //
    // public void setUncheck(String uncheck) {
    // this.uncheck = uncheck;
    // }
    //
    // }

}