package se.vgregion.ifeed.types;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldInfTest {

    // @Test
    public void toAndFromCsvRow() {
        FieldInf one = new FieldInf();
        String csvRow = one.toCsvRow();
        FieldInf copy = FieldInf.fromCsvRow(csvRow);
        Assert.assertEquals(csvRow, copy.toCsvRow());
    }

}