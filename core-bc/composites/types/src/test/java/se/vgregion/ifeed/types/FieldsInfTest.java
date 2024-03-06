package se.vgregion.ifeed.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldsInfTest {

    @Test
    public void getFieldInfs() throws IOException {
        FieldsInf fi = new FieldsInf();
        fi.setText(loadFieldsConfigText());
        List<FieldInf> result = fi.getFieldInfs();
        System.out.println(result + " \n\n" + result.size());
        Assert.assertTrue(!result.isEmpty());
    }

    public static FieldInf getSampleFieldInf() {
        FieldsInf fi = new FieldsInf();
        fi.setText(loadFieldsConfigText());
        List<FieldInf> result = fi.getFieldInfs();
        return new FieldInf(result);
    }

    @Test
    public void getAllIds() {
        Map<String, Object> feedContent = new HashMap<>();
        FieldsInf fi = new FieldsInf();
        fi.setText(loadFieldsConfigText());
        List<FieldInf> result = fi.getFieldInfs();
        FieldInf root = new FieldInf(result);
        System.out.println(root.getAllIds());
    }

    @Ignore
    @Test
    public void produceJson() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FieldsInf fi = new FieldsInf();
        fi.setText(loadFieldsConfigText());
        List<FieldInf> result = fi.getFieldInfs();
        String json = gson.toJson(result);
        System.out.println(
                json
        );

        System.out.println(json.length());
    }

    public static String loadSomeAlfrescoDoc() {
        try {
            return loadResource("/some-alfresco-doc.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadFieldsConfigText() {
        try {
            return loadResource("/fields-config.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadAlfrescoSampleDoc() throws IOException {
        return loadResource("/alfresco-sample-doc.json");
    }

    private static String loadResource(String name) throws IOException {
        URL url = FieldsInfTest.class.getResource(name);
        FileReader fr = new FileReader(url.getFile());
        FieldsInf fi = new FieldsInf();

        StringBuffer sb = new StringBuffer();
        for (int c = 0; c != -1; c = fr.read()) {
            sb.append((char) c);
        }
        return sb.toString().trim();
    }

    @Test
    public void getDataFromCache() {
        FieldsInf.putDataIntoCache("[]");
        System.out.println(FieldsInf.getDataFromCache());
    }

    @Ignore
    @Test
    public void getFieldInfsFromTabularText() throws IOException {
        FieldsInf fi = new FieldsInf();
        // File resourcesDirectory = new File("src/test/resources");
        String toTypes = (new File(".").getAbsolutePath().replace(".", ""));
        Path path = Paths.get(toTypes, "src", "test", "resources", "fields-config.txt");
        fi.setText(new String(Files.readAllBytes(path)));
        List<FieldInf> rows = fi.getFieldInfsFromTabularText();
        for (FieldInf row : rows) {
            String template = "result.add(new LabelledValue(\"%s\", \"%s\"));";
            //System.out.println(row.getName());
            String heading = String.format(template, row.getId(), row.getName());
            System.out.println(heading);
            for (FieldInf child : row.getChildren()) {
                // result.add(new LabelledValue("core:ArchivalObject.idType", "N/A"));
                if (child.getInHtmlView()) {
                    String signature = String.format(template, child.getId(), child.getName());
                    System.out.println(signature);
                }
                // System.out.println(child.getId() + " '" + child.getName() + "'" + child.isInHtmlView());
            }
        }
    }

}
