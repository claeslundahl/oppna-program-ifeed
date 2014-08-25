package se.vgregion.ifeed.client;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import se.vgregion.ifeed.shared.ColumnDef;

/**
 * Created by clalu4 on 2014-03-14.
 */
public class ColumnDefTest extends TestCase {

    @Test
    public void testParseAndSet() throws Exception {
        ColumnDef columnDef = new ColumnDef();
        columnDef.parseAndSet("dc.title|Titel|left|70");
        Assert.assertEquals("dc.title", columnDef.getName());
        Assert.assertEquals("Titel", columnDef.getLabel());
        Assert.assertEquals("left", columnDef.getAlignment());
        Assert.assertEquals("70", columnDef.getWidth());
    }

}
