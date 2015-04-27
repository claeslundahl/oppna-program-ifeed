package se.vgregion.ifeed.types;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

public class IFeedTest {
    private IFeed makeSample() {
        VgrDepartment department = new VgrDepartment();
        department.setName("name");
        department.setDescription("desc");
        department.setId(-1001l);
        department.setOpen(true);

        VgrGroup group = new VgrGroup();
        group.setName("group-name");
        group.setId(1002l);
        group.setDescription("group-desc");

        IFeedFilter filter = new IFeedFilter();
        filter.setFilterQuery("Test*");
        filter.setFilterKey("DC.title");

        IFeed feed = new IFeed();
        feed.setId(-1l);
        feed.setVersion(1l);
        feed.setCreatorName("cn");
        feed.setDescription("feed-desc");
        feed.setLinkNativeDocument(true);
        feed.setSortField("sortField");

        feed.setDepartment(department);
        feed.setGroup(group);
        feed.addFilter(filter);

        return feed;
    }

    public String toJson() {
        String s = makeSample().toJson();
        System.out.println(s);
        return s;
    }

    @Test
    public void toIFeed() {
        IFeed r = IFeed.fromJson(toJson());
        Assert.assertEquals(makeSample(), r);
    }
}