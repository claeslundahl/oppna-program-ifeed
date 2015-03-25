package se.vgregion.ifeed.scheduler;

import com.liferay.portal.kernel.messaging.Message;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IFeedPublishSchedulerTest {

    @Test
    public void receive() {
        IFeedPublishScheduler iFeedPublishScheduler = new IFeedPublishScheduler() {
            @Override
            protected void loadContext(String configLocation) {
            }
        };

        ApplicationContext context = mock(ApplicationContext.class);

        IFeedService iFeedService = mock(IFeedService.class);
        IFeedSolrQuery iFeedSolrQuery = mock(IFeedSolrQuery.class);

        when(context.getBean(IFeedService.class)).thenReturn(iFeedService);
        when(context.getBean(IFeedSolrQuery.class)).thenReturn(iFeedSolrQuery);

        List<IFeed> ifeeds = new ArrayList<IFeed>();
        IFeed firstResult = new IFeed();
        ifeeds.add(firstResult);

        when(iFeedService.getIFeeds()).thenReturn(ifeeds);

        iFeedPublishScheduler.setContext(context);

        List<Map<String, Object>> iFeedResults = new ArrayList<Map<String, Object>>();
        Map<String, Object> someValue = new HashMap<String, Object>();
        iFeedResults.add(someValue);

        when(iFeedSolrQuery.getIFeedResults(firstResult, null)).thenReturn(iFeedResults);

        Message message = mock(Message.class);
        iFeedPublishScheduler.receive(message);
    }

}
