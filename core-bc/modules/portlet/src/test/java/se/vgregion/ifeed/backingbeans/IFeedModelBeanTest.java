package se.vgregion.ifeed.backingbeans;

import junit.framework.TestCase;
import org.junit.Test;
import se.vgregion.ifeed.types.IFeed;

import java.io.IOException;

public class IFeedModelBeanTest {

    @Test
    public void toJson() {
        IFeedModelBean iFeedModelBean = new IFeedModelBean();
        IFeed iFeed = new IFeed();

        System.out.println(iFeedModelBean.toJson());
        // System.out.println(iFeed.toJson());
    }

    @Test
    public void clearBean() throws IOException {
        IFeedModelBean.clearBean(ApplicationTest.getAlfrescoFeed());
    }

}
