package se.vgregion.ifeed.formbean;

import java.io.Serializable;

import se.vgregion.ldap.HasCommonLdapFields;

public class BaseVgrOrganization implements Serializable, HasCommonLdapFields {

    private String dn;
    private String ou;
    private String io;
    private String cn;
    private String type;
    private String hsaIdentity;
    private boolean leaf;

    public BaseVgrOrganization() {

    }

    public BaseVgrOrganization(String dn, String hsaIdentity) {
        setDn(dn);
        setHsaIdentity(hsaIdentity);
    }

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

    public String getIo() {
        return io;
    }

    public void setIo(String io) {
        this.io = io;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHsaIdentity() {
        return hsaIdentity;
    }

    public void setHsaIdentity(String hsaIdentity) {
        this.hsaIdentity = hsaIdentity;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

}