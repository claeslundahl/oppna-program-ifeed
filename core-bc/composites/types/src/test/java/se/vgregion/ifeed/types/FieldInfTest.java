package se.vgregion.ifeed.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static se.vgregion.ifeed.types.FieldsInfTest.getSampleFieldInf;

public class FieldInfTest {

    // @Test
    public void toAndFromCsvRow() {
        FieldInf one = new FieldInf();
        String csvRow = one.toCsvRow();
        FieldInf copy = FieldInf.fromCsvRow(csvRow);
        Assert.assertEquals(csvRow, copy.toCsvRow());
    }

    @Test
    public void initForMiniView() {
        FieldInf fi = getSampleFieldInf();
        fi.init();

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> doc = new GsonBuilder().create().fromJson(FieldsInfTest.loadSomeAlfrescoDoc(), type);

        fi.initForMiniView(doc);
        fi.getChildren().get(0);
    }

    final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void initForMaxView() {
        FieldInf fi = getSampleFieldInf();
        fi.init();

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> doc = gson.fromJson(FieldsInfTest.loadSomeAlfrescoDoc(), type);

        fi.initForMaxView(doc);
        System.out.println(fi.getChildren().get(0));
    }


    public static void main(String[] args) {
        FieldInf fi = getSampleFieldInf();
        fi.init();
        Map<String, Object> doc = new HashMap<String, Object>(){
            @Override
            public Object get(Object key) {
                return key;
            }

            @Override
            public boolean containsKey(Object key) {
                return true;
            }
        };
        fi.initForMiniView(doc);
        fi.visit(new FieldInf.Visitor() {
            @Override
            public void each(FieldInf item) {
                if (item.getId() != null && !item.getId().trim().equals("") && !item.getId().startsWith("dc.")) {
                    item.getParent().getChildren().remove(item);
                }
            }
        });
        FieldInf tooltip = fi.getChildren().get(0);
        System.out.println(gson.toJson(tooltip));

    }

}