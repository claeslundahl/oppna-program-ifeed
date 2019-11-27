package se.vgregion.ldap;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LdapApi {

    private String initialContextFactoryClassName = "com.sun.jndi.ldap.LdapCtxFactory";

    private String url;

    private String user, password, base;

    private int resultLimit = 500;

    private String[] attributeFilter = {"*"};

    private static LdapApi instance;

    public LdapApi() {
        super();
    }

    public LdapApi(String initialContextFactoryClassName, String url, String user, String password, String base) {
        this();
        this.initialContextFactoryClassName = initialContextFactoryClassName;
        this.url = url;
        this.user = user;
        this.password = password;
        this.base = base;
    }

    public LdapApi(String url, String user, String password, String base) {
        this();
        this.url = url;
        this.user = user;
        this.password = password;
        this.base = base;
    }

    public List<Map<String, Object>> query(String filter) {
        try {
            return queryImp(filter);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, Object>> queryImp(String filter) throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryClassName);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext context = new InitialDirContext(env);

        SearchControls sc = new SearchControls();

        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration results = context.search(base, filter, sc);
        List<Map<String, Object>> result = toMaps(results);
        context.close();
        return result;
    }

    public List<Map<String, Object>> toMaps(NamingEnumeration results) {
        try {
            return toMapsImpl(results, resultLimit);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> toMaps(NamingEnumeration results, Integer resultLimit) {
        try {
            return toMapsImpl(results, resultLimit);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Map<String, Object>> toMapsImpl(NamingEnumeration results, Integer resultLimit) throws NamingException {
        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        while (results.hasMore()) {
            SearchResult sr = (SearchResult) results.next();
            Map<String, Object> map = toMap(sr.getAttributes());
            result.add(map);
            if (map.containsKey("dn")) throw new RuntimeException();
            map.put("dn", sr.getNameInNamespace());
            count++;
            if (resultLimit != null && count > resultLimit)
                break;
        }
        return result;
    }

    static private Map<String, Object> toMap(Attributes attributes) {
        try {
            return toMapImp(attributes);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    static private Map<String, Object> toMapImp(Attributes attributes) throws NamingException {
        Map<String, Object> result = new TreeMap<>();
        NamingEnumeration<? extends Attribute> all = attributes.getAll();
        while (all.hasMore()) {
            Attribute item = all.next();
            result.put(item.getID(), item.get());
        }
        return result;
    }


    public String getInitialContextFactoryClassName() {
        return initialContextFactoryClassName;
    }

    public void setInitialContextFactoryClassName(String initialContextFactoryClassName) {
        this.initialContextFactoryClassName = initialContextFactoryClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public int getResultLimit() {
        return resultLimit;
    }

    public void setResultLimit(int resultLimit) {
        this.resultLimit = resultLimit;
    }

    public String[] getAttributeFilter() {
        return attributeFilter;
    }

    public void setAttributeFilter(String[] attributeFilter) {
        this.attributeFilter = attributeFilter;
    }


    public static LdapApi newInstanceFromConfig() {
        try {
            if (instance != null) {
                return instance;
            }
            Properties prop = fetchProperties();
            instance = new LdapApi(
                    prop.getProperty("ldap.org.authentication.java.naming.provider.url"),
                    prop.getProperty("ldap.org.synchronization.java.naming.security.principal"),
                    prop.getProperty("ldap.org.synchronization.java.naming.security.credentials"),
                    prop.getProperty("ldap.org.synchronization.userSearchBase")
            );
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    static Properties fetchProperties() {
        try {
            return fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Properties fetchProperties(Path path) throws IOException {
        Properties properties = new Properties();
        InputStream src = Files.newInputStream(path);
        properties.load(new InputStreamReader(src));
        return properties;
    }

}
