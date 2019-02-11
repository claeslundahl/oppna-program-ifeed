package se.vgregion.ifeed.viewer;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColumnTest {

    @Test
    public void constructor() {
        Column column = new Column("dc.title|Titel (autokomplettering)|left|70");
        assertEquals("dc.title", column.getKey());
        assertEquals("Titel (autokomplettering)", column.getTitle());
        assertEquals("left", column.getAlignment());
        assertEquals("70", column.getWidth());

        System.out.println(column);
    }

}