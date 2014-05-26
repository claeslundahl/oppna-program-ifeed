package se.vgregion.ifeed.client;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2014-03-14.
 */
public class TableDefTest extends TestCase {

    @Test
    public void testCreateColumnDefs() throws Exception {
        TableDef tableDef = new TableDef();
        tableDef.createColumnDefs("dc.title|Titel|left|70,dcterms.audience|Audience|right|30");
        List<ColumnDef> columns = tableDef.getColumnDefs();
        Assert.assertEquals(2, columns.size());
    }

}
