package se.vgregion.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.springframework.ldap.core.LdapTemplate;

public class LdapSupportServiceImp implements LdapSupportService {

    private LdapTemplate ldapTemplate;

    // @Override
    // public List<VgrOrganization> findAllFirstLevel() {
    // VgrOrganization org = new VgrOrganization();
    // org.setDn("Ou=org");
    // return findChildNodes(org);
    // }

    private String nonsensFilter = "(objectClass=*)";

    @Override
    public <T extends HasCommonLdapFields> List<T> findChildNodes(T org) {
        List<T> result = new ArrayList<T>();
        try {
            LdapBeanContextMapper<T> mapper = new LdapBeanContextMapper<T>((Class<T>) org.getClass());
            result.addAll(getLdapTemplate().search(org.getDn(), nonsensFilter, SearchControls.ONELEVEL_SCOPE,
                    mapper));
            return result;
        } catch (Exception e) {

            e.printStackTrace();

            return result;
        }
    }

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

}
