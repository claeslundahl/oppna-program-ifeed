package se.vgregion.ifeed.service;

import se.vgregion.ifeed.service.ifeed.ObjectRepo;
import se.vgregion.ifeed.types.User;

import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class LdapLoginServiceTest {


    public static void main(String[] agrs) throws IOException {
        Properties config = new Properties();

        config.load(
            Files.newBufferedReader(
                Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties")
            )
        );

        LdapLoginService service = new LdapLoginService();
        service.setServiceUserDN(config.getProperty("ldap.service.user.dn"));
        service.setServiceUserPassword(config.getProperty("ldap.service.user.password"));
        service.setBase(config.getProperty("ldap.search.base"));
        service.setLdapUrl(config.getProperty("ldap.url"));

        service.setObjectRepo(new ObjectRepo() {
            @Override
            public <T> T findByPrimaryKey(Class<T> clazz, Object id) {
                return (T) new User("testUser");
            }

            @Override
            public void persist(Object entity) {

            }
        });

        try {
            service.login("that", "willFail");
        } catch (FailedLoginException e) {
            // OK.
        }
    }

}