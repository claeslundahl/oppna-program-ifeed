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

            //
            // List<T> result = new ArrayList<T>();
            // T vo = null;
            // try {
            // vo = (T) org.getClass().newInstance();
            // } catch (InstantiationException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // } catch (IllegalAccessException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }
            // int c = 0;
            // if (org.getDn().matches(".* [0-9]+")) {
            // String[] parts = org.getDn().split(Pattern.quote(" "));
            // c = Integer.parseInt(parts[parts.length - 1]) + 1;
            // }
            // vo.setDn("Dummy " + c);
            // result.add(vo);
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
