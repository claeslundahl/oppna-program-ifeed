package se.vgregion.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import se.vgregion.common.utils.BeanMap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.HashMap;
import java.util.Map;


public class LdapBeanContextMapper<T extends Object> implements ContextMapper {

    private Class<T> beanType;

    public LdapBeanContextMapper(Class<T> beanType) {
        setBeanType(beanType);
    }

    @Override
    public T mapFromContext(Object context) {
        DirContextAdapter ctx = (DirContextAdapter) context;
        try {
            T bean = beanType.newInstance();
            BeanMap bm = new BeanMap(bean);

            Attributes attributes = ctx.getAttributes();

            if (bean instanceof HasCommonLdapFields) {
                HasCommonLdapFields hclf = (HasCommonLdapFields) bean;
                hclf.setDn(ctx.getDn().toString());
                hclf.setOu(hclf.getOu());
            }

            NamingEnumeration<String> names = attributes.getIDs();

            Map<String, Object> fromLdap = new HashMap<>();

            while (names.hasMore()) {
                String name = names.next();
                Attribute attribute = attributes.get(name);
                Object value = attribute.get();
                if (bm.containsKey(name)) {
                    bm.put(name, value);
                }
                fromLdap.put(name, value);
            }
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NamingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Class<T> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<T> beanType) {
        this.beanType = beanType;
    }

}
