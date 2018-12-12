package se.vgregion.ifeed.service.solr;

import se.vgregion.common.utils.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OldIndexData {

    private static Map<String, List<Object>> result;

    public static Map<String, List<Object>> getCachedAlfrescoBariumValues() {
        try {
            return getCachedAlfrescoBariumValuesImpl();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, List<Object>> getCachedAlfrescoBariumValuesImpl() throws IOException {
        if (result == null) {
            Path toTheFile = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "alfresco-barium-values.json");
            byte[] content = Files.readAllBytes(toTheFile);
            result = Json.toObject(TreeMap.class, new String(content));
        }
        return result;
    }

}
