package se.vgregion.ifeed.server;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by clalu4 on 2015-07-02.
 */
public class SplitterTest {

    @org.junit.Test
    public void splitOnChar() {
        List<String> r = Splitter.splitOnChar("\\foo\\baar\\baaz\\", '\\');
        Assert.assertEquals("[\\, foo, \\, baar, \\, baaz, \\]", r.toString());
        r = Splitter.splitOnChar("foo\\baar\\baaz", '\\');
        Assert.assertEquals("[foo, \\, baar, \\, baaz]", r.toString());
        r = Splitter.splitOnChar("foo", '\\');
        Assert.assertEquals("[foo]", r.toString());
    }

}