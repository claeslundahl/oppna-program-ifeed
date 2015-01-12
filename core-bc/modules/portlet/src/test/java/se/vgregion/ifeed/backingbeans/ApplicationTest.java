package se.vgregion.ifeed.backingbeans;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class ApplicationTest {

    @Test
    public void getMaxPageCountImp() {
        Collection list = new ArrayList();
        for (int i = 0; i < 101; i++) {
            list.add(i);
        }

        int result = Application.getMaxPageCountImp(list, 10);
        Assert.assertEquals(11, result);

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
    }

}