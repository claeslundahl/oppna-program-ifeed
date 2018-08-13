package se.vgregion.ifeed.types;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

}
