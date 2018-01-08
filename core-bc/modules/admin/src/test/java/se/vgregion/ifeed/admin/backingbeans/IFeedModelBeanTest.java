package se.vgregion.ifeed.admin.backingbeans;

import junit.framework.TestCase;
import org.junit.Test;
import se.vgregion.ifeed.types.IFeed;

public class IFeedModelBeanTest {

    @Test
    public void toJson() {
        IFeedModelBean iFeedModelBean = new IFeedModelBean();
        IFeed iFeed = new IFeed();

        System.out.println(iFeedModelBean.toJson());
        System.out.println(iFeed.toJson());
    }

}