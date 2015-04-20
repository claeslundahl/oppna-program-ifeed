package se.vgregion.ifeed.client;

import junit.framework.Assert;
import org.apache.commons.collections.BeanMap;
import org.junit.Test;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;

import java.util.HashMap;

/**
 * Created by clalu4 on 2014-03-14.
 */
public class ColumnDefTest {

    @Test
    public void testParseAndSet() throws Exception {
        ColumnDef columnDef = new ColumnDef();
        columnDef.parseAndSet("dc.title|Titel|left|70");
        Assert.assertEquals("dc.title", columnDef.getName());
        Assert.assertEquals("Titel", columnDef.getLabel());
        Assert.assertEquals("left", columnDef.getAlignment());
        Assert.assertEquals("70", columnDef.getWidth());
    }

    @Test
    public void main() {
        System.out.println("aaarrhg");
        DynamicTableDef cd = new DynamicTableDef();
        BeanMap bm = new BeanMap(cd);
        HashMap hm = new HashMap(bm);

        for (Object key : hm.keySet())
            System.out.println("map.put(\"" + key + "\", " + key + ");");
    }

}
