package se.vgregion.ifeed.backingbeans;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ApplicationTest {

    @Test
    public void getMaxPageCountImp() {
        Collection list = new ArrayList();
        for (int i = 0; i < 30; i++) {
            list.add(i);
        }

        Application app = new Application(new IFeedServiceImpl(){
            @Override
            public int getLatestFilterQueryTotalCount() {
                return 30;
            };
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

}