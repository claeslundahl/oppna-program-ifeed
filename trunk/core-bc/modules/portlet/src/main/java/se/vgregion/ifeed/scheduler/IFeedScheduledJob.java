package se.vgregion.ifeed.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.ifeed.metadata.service.MetadataService;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

public class IFeedScheduledJob implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedScheduledJob.class);

    private MetadataService service;
    @Override
    public void receive(Message message) {
        System.out.println("Scheduler started");
        LOGGER.debug("Schedule task is started {}", message);
        LOGGER.debug("Trying to get hold of an application context");
        LOGGER.debug("Metadataservice is: {}", service);
        ApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext("classpath*:spring/ifeed-*.xml");
        } catch(BeansException e) {
            LOGGER.error("Context is null, failed to inialize: {}", e.getCause());
            return;
        }

        LOGGER.debug("Context created: {}", context);
        service = context.getBean("metadataService", MetadataService.class);
        if (service != null) {
            LOGGER.debug("Importing metadata");
            service.importMetadata();
        } else {
            LOGGER.error("Couldn't find bean for MetadataService");
        }
    }
}
