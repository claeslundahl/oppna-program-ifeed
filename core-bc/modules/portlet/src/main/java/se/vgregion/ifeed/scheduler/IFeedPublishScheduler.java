package se.vgregion.ifeed.scheduler;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class IFeedPublishScheduler implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedPublishScheduler.class);

    private ApplicationContext context;
    private IFeedService iFeedService;
    private IFeedSolrQuery iFeedSolrQuery;

    @Override
    public final void receive(final Message message) {
        LOGGER.debug("Schedule task is started {}", ToStringBuilder.reflectionToString(message));

        loadContext("classpath*:spring/ifeed-*.xml");
        initBeans();

        Collection<IFeed> modifiedIFeeds = new HashSet<IFeed>();

        Collection<IFeed> ifeeds = iFeedService.getIFeeds();
        for (IFeed iFeed : ifeeds) {
            LOGGER.debug("Checking for updates since {} in feed {} (id: {})", new Object[] { iFeed.getTimestamp(),
                    iFeed.getName(), iFeed.getId() });
            Collection<Map<String, Object>> iFeedResults = iFeedSolrQuery.getIFeedResults(iFeed,
                    iFeed.getTimestamp(), null);

            if (!isEmpty(iFeedResults) || iFeed.getTimestamp() == null) {
                LOGGER.debug("{} new documents found in feed {} (id: {})", new Object[] { iFeedResults.size(),
                        iFeed.getName(), iFeed.getId() });
                LOGGER.debug("Sending feed {} (id: {}) to PuSH server.",
                        new Object[] { iFeed.getName(), iFeed.getId() });
                modifiedIFeeds.add(iFeed);
            }
        }

        if (modifiedIFeeds.size() > 0) {
            for (IFeed iFeed : modifiedIFeeds) {
                iFeedService.updateIFeed(iFeed);
            }
        }
    }

    /**
     *
     */
    private void initBeans() {
        iFeedService = context.getBean(IFeedService.class);
        iFeedSolrQuery = context.getBean(IFeedSolrQuery.class);
    }

    /**
     * @param configLocation
     */
    protected void loadContext(String configLocation) {
        LOGGER.debug("Loading spring context");
        if (context == null) {
            try {
                LOGGER.debug("Creating new application context using config " + "location: {}", configLocation);
                context = new ClassPathXmlApplicationContext(configLocation);
                LOGGER.debug("Context created: {}", context);
            } catch (BeansException e) {
                e.printStackTrace();
                LOGGER.error("Context is null, failed to inialize: {}", e.getCause());
            }
        }
    }

    /**
     * @param c
     * @return
     */
    private boolean isEmpty(final Collection<?> c) {
        return (c == null || c.size() == 0);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        IFeedPublishScheduler scheduler = new IFeedPublishScheduler();
        scheduler.receive(null);
    }

    protected ApplicationContext getContext() {
        return context;
    }

    protected void setContext(ApplicationContext context) {
        this.context = context;
    }

}
