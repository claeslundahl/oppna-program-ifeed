package se.vgregion.ifeed.formbean;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriTemplate;

import se.vgregion.ifeed.formbean.IFeedFormBeanList.IFeedFormBean;
import se.vgregion.ifeed.types.IFeed;

public class IFeedFormBeanListTest {

    IFeedFormBeanList list;
    private List<IFeed> iFeedList;
    private UriTemplate iFeedAtomFeed;
    private IFeed feed;

    @Before
    public void setUp() throws Exception {
        iFeedList = new ArrayList<IFeed>();
        feed = new IFeed();
        iFeedList.add(feed);

        iFeedAtomFeed = new UriTemplate("https://vgregion.se");

        list = new IFeedFormBeanList(iFeedList, iFeedAtomFeed);
    }

    @Test
    public void size() {
        Assert.assertEquals(1, list.size());
    }

    /*@Test
    public void getInt() {
        IFeedFormBean bean = list.get(0);
        Assert.assertEquals(feed, list.get(0).getiFeed());
    }*/

}
