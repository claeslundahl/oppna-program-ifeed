package se.vgregion.ifeed.types;

import junit.framework.Assert;
import net.sf.cglib.beans.BeanMap;
import org.junit.Test;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class IFeedTest {

    @Test
    public void spanLogik() {
        IFeed feed = makeSample();
        feed.addFilter(new IFeedFilter("fk", "greater", "2020-01-01"));
        System.out.println(feed.toQuery(null));
    }

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

}