package se.vgregion.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import se.vgregion.common.utils.BeanMap;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;


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

            while (names.hasMore()) {
                String name = names.next();
                Attribute attribute = attributes.get(name);
                Object value = attribute.get();
                // System.out.println(name + ": " + value);
                if (bm.containsKey(name)) {
                    bm.put(name, value);
                }
            }

            // System.out.println(new HashMap(bm) + "\n");

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
