package se.vgregion.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.*;

public class LdapApiTest {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties");
        System.out.println(path);
        Properties properties = new Properties();
        InputStream src = Files.newInputStream(path);
        properties.load(new InputStreamReader(src));
        for (Object o : properties.keySet()) {
            System.out.println(o);
        }
    }

}