package se.vgregion.ifeed.backingbeans;

import com.google.gson.GsonBuilder;
import junit.framework.Assert;
import org.junit.Test;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class ApplicationTest {

    @Test
    public void getMaxPageCountImp() {
        Collection list = new ArrayList();
        for (int i = 0; i < 30; i++) {
            list.add(i);
        }

        Application app = new Application(new IFeedServiceImpl() {
            @Override
            public int getLatestFilterQueryTotalCount() {
                return 30;
            }

            ;
        });

        int result = app.getMaxPageCountImp(list, 25);
        Assert.assertEquals(2, result);

        /*

        list.remove(100);
        result = Application.getMaxPageCountImp(list, 10);
        Assert.assertEquals(10, result);

        list.remove(99);
        result = Application.getMaxPageCountImp(list, 10);
        Assert.assertEquals(10, result);

        list.clear();
        list.add(1);
        result = Application.getMaxPageCountImp(list, 10);
        Assert.assertEquals(1, result);
*/
    }

    @Test
    public void gettingOfLong() {
        String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
        String[] r = "hej hoppsan (89)".split("\\(|\\)");
        System.out.println(Arrays.asList(r));
    }

    @Test
    public void getFilterFieldsForBothMetadataSets() throws IOException {
        List<FieldInf> result = Application.getFilterFieldsForBothMetadataSets(getFieldInfs());
        System.out.println(result);
    }



    static IFeed getSofiaFeed() throws IOException {
        return getFeed("sofia-feed");
    }

    static IFeed getAlfrescoFeed() throws IOException {
        return getFeed("alfresco-feed");
    }

    static IFeed getFeed(String fileName) throws IOException {
        Path toProject = Paths.get(new File(".").getAbsolutePath()).getParent().getParent().getParent().getParent();
        String path = String.format("core-bc\\composites\\types\\src\\test\\resources\\%s.json", fileName)
                .replace("\\", File.separator);
        Path filePath = Paths.get(toProject.toString(), path);
        return new GsonBuilder().create().fromJson(Files.readString(filePath), IFeed.class);
    }

    static List<FieldInf> getFieldInfs() throws IOException {
        Path toProject = Paths.get(new File(".").getAbsolutePath()).getParent().getParent().getParent().getParent();
        String path = "core-bc\\composites\\types\\src\\test\\resources\\fields-config.json".replace("\\", File.separator);
        Path filePath = Paths.get(toProject.toString(), path);
        System.out.println(Files.exists(filePath));
        FieldsInf fi = new FieldsInf();
        fi.setText(Files.readString(filePath));
        List<FieldInf> fields = fi.getFieldInfs();
        return fields;
    }

    @Test
    public void isBlendingMetadataSpecifications() throws IOException {
        IFeed sofia = getSofiaFeed();
        Assert.assertTrue(Application.isBlendingMetadataSpecifications(sofia, getFieldInfs()));
        sofia.getComposites().clear();
        Assert.assertFalse(Application.isBlendingMetadataSpecifications(sofia, getFieldInfs()));
    }

}
