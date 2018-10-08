package se.vgregion.ifeed.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FieldsInfTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    //@Ignore
    public void getFieldInfs() throws IOException {
        FieldsInf fi = new FieldsInf();
        fi.setText(loadFieldsConfigText());
        List<FieldInf> result = fi.getFieldInfs();
        System.out.println(result + " \n\n" + result.size());
        Assert.assertTrue(!result.isEmpty());
    }

    @Test
    public void putFieldInfInto() throws IOException {
        FieldsInf fi = new FieldsInf();
        fi.setText(loadFieldsConfigText());
        List<FieldInf> result = fi.getFieldInfs();

        IFeedFilter filter = new IFeedFilter();
        filter.setFilterKey("DC.title");
        fi.putFieldInfInto(filter);
        System.out.println(filter.getFieldInf().getName());
    }

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

    private String loadFieldsConfigText() throws IOException {
        URL url = FieldsInfTest.class.getResource("/fields-config.json");
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
                if (child.isInHtmlView()) {
                    String signature = String.format(template, child.getId(), child.getName());
                    System.out.println(signature);
                }
                // System.out.println(child.getId() + " '" + child.getName() + "'" + child.isInHtmlView());
            }
        }
    }

}
