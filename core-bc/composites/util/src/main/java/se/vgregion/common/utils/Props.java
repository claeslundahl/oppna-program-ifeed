package se.vgregion.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Props {

    public static Properties fetchProperties() {
        try {
            return fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties fetchLocalBackupProperties() {
        try {
            return fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  static Properties fetchProperties(Path path) throws IOException {
        Properties properties = new Properties();
        InputStream src = Files.newInputStream(path);
        properties.load(new InputStreamReader(src));
        return properties;
    }

}
