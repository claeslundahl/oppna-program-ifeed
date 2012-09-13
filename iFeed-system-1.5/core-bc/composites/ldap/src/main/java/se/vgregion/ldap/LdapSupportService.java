package se.vgregion.ldap;

import java.util.List;

public interface LdapSupportService {

    public <T extends HasCommonLdapFields> List<T> findChildNodes(T org);

    public <T extends HasCommonLdapFields> List<T> find(T org);

    public <T extends HasCommonLdapFields, R> List<R> find(T org, Class<R> resultType);

}
