package se.vgregion.ifeed.service.ifeed;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.service.push.IFeedPublisher;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

public class IFeedServiceImplTest {

    IFeedServiceImpl service;
    private JpaRepository<IFeed, Long, Long> iFeedRepoParam;
    private JpaRepository<FieldsInf, Long, Long> fieldsInfParam;
    private IFeedPublisher iFeedPublisher;

    @Before
    public void setUp() throws Exception {
        iFeedRepoParam = mock(JpaRepository.class);
        iFeedPublisher = mock(IFeedPublisher.class);

        service = new IFeedServiceImpl(iFeedRepoParam, iFeedPublisher, fieldsInfParam);
    }

    @Test
    public void getIFeeds() {
        Collection<IFeed> value = new ArrayList<IFeed>();
        when(iFeedRepoParam.findAll()).thenReturn(value);
        List<IFeed> result = service.getIFeeds();
        Assert.assertEquals(value, result);
    }

    @Test
    public void getUserIFeeds() {
        String userId = "usr1";
        service.getUserIFeeds(userId);
        // Smoke-test
    }

    @Test
    public void getIFeed() {
        Long id = 101l;
        IFeed value = new IFeed();
        value.setId(id);
        when(iFeedRepoParam.find(id)).thenReturn(value);
        IFeed result = service.getIFeed(id);
        Assert.assertEquals(id, value.getId());
    }

    @Test
    public void addIFeed() {
        IFeed iFeed = new IFeed();
        service.addIFeed(iFeed);
        Mockito.verify(iFeedRepoParam).store(iFeed);
    }

    @Test
    public void removeIFeed() {
        Long id = 101l;
        service.removeIFeed(id);
        Mockito.verify(iFeedRepoParam).remove(id);
    }

    @Test
    public void updateIFeed() {
        Long id = 101l;
        IFeed newFeed = new IFeed();
        newFeed.setId(id);

        IFeed oldFeed = new IFeed();
        when(iFeedRepoParam.find(id)).thenReturn(oldFeed);

        IFeedFilter filter = new IFeedFilter();
        oldFeed.addFilter(filter);
        newFeed.addFilter(filter);

        IFeed mergedFeed = new IFeed();
        mergedFeed.setId(id);
        when(iFeedRepoParam.merge(oldFeed)).thenReturn(mergedFeed);

        service.updateIFeed(newFeed);

        Mockito.verify(iFeedRepoParam).merge(newFeed);
    }

}
