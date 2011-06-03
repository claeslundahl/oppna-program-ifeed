/**
 * 
 */
package se.vgregion.ifeed.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.push.IFeedPublisher;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class IFeedPublishScheduler implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedPublishScheduler.class);

    private IFeedPublisher iFeedPublisher;
    private IFeedService iFeedService;
    private IFeedSolrQuery iFeedSolrQuery;

    @Override
    public void receive(Message message) {
        LOGGER.debug("Schedule task is started {}", ToStringBuilder.reflectionToString(message));

        setupContext();
        Collection<IFeed> modifiedIFeeds = new HashSet<IFeed>();

        Collection<IFeed> ifeeds = iFeedService.getIFeeds();
        for (IFeed iFeed : ifeeds) {
            LOGGER.debug("Checking for updates since {} in feed {} (id: {})", new Object[] { iFeed.getTimestamp(),
                    iFeed.getName(), iFeed.getId() });
            Collection<Map<String, Object>> iFeedResults = iFeedSolrQuery.getIFeedResults(iFeed,
                    iFeed.getTimestamp());
            if (!isEmpty(iFeedResults)) {
                LOGGER.debug("{} new documents found in feed {} (id: {})", new Object[] { iFeedResults.size(),
                        iFeed.getName(), iFeed.getId() });
                LOGGER.debug("Sending feed {} (id: {}) to PuSH server.",
                        new Object[] { iFeed.getName(), iFeed.getId() });
                iFeedPublisher.addIFeed(iFeed);
                modifiedIFeeds.add(iFeed);
            }
        }

        if (modifiedIFeeds.size() > 0 && iFeedPublisher.publish()) {
            for (IFeed iFeed : modifiedIFeeds) {
                iFeed.setTimestamp();
                iFeedService.updateIFeed(iFeed);
            }
        }
    }

    private void setupContext() {
        LOGGER.debug("Trying to get hold of an application context");
        ApplicationContext context = null;

        try {
            context = new ClassPathXmlApplicationContext("classpath*:spring/ifeed-*.xml");
            iFeedPublisher = context.getBean(IFeedPublisher.class);
            iFeedService = context.getBean(IFeedService.class);
            iFeedSolrQuery = context.getBean(IFeedSolrQuery.class);
        } catch (BeansException e) {
            e.printStackTrace();
            LOGGER.error("Context is null, failed to inialize: {}", e.getCause());
            return;
        }
    }

    private boolean isEmpty(Collection<?> c) {
        return (c == null || c.size() == 0);
    }

    public static void main(String[] args) {
        IFeedPublishScheduler scheduler = new IFeedPublishScheduler();
        scheduler.receive(null);
    }
}
