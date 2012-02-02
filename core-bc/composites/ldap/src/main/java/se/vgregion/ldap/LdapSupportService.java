package se.vgregion.ldap;

import java.util.List;

public interface LdapSupportService {

    public <T extends HasCommonLdapFields> List<T> findChildNodes(T org);

}
