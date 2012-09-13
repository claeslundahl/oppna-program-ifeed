package se.vgregion.ldap;

public interface HasCommonLdapFields {

    String getDn();

    void setDn(String dn);

    String getOu();

    void setOu(String ou);

}
