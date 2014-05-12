package se.vgregion.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.apache.commons.beanutils.BeanMap;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextSource;
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

    @Override
    public <T extends HasCommonLdapFields> List<T> find(T org) {
        return find(org, (Class<T>) org.getClass());
    }

    String mkFilter(Object org) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> bm = new HashMap<String, Object>(new BeanMap(org));
        bm.remove("class");
        bm.remove("dn");

        int count = 0;
        for (String key : new TreeSet<String>(bm.keySet())) {
            Object value = bm.get(key);
            if (value instanceof String) {
                count++;
                sb.append("(");
                sb.append(key);
                sb.append("=");
                sb.append(value);
                sb.append(")");
            }
        }

        if (count == 0) {
            return nonsensFilter;
        }

        if (count > 1) {
            return "(&" + sb + ")";
        }

        return sb.toString();
    }

    @Override
    public <T extends HasCommonLdapFields, R> List<R> find(T org, Class<R> resultType) {
        List<R> result = new ArrayList<R>();
        try {
            LdapBeanContextMapper<R> mapper = new LdapBeanContextMapper<R>(resultType);
            result.addAll(getLdapTemplate().search(org.getDn(), mkFilter(org), SearchControls.SUBTREE_SCOPE,
                    mapper));
            return result;
        } catch (Exception e) {
            // e.printStackTrace();
            return result;
        }
    }



}
