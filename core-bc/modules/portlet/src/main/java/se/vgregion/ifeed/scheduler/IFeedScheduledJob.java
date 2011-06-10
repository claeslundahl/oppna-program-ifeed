package se.vgregion.ifeed.scheduler;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.ifeed.service.metadata.MetadataService;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

public class IFeedScheduledJob implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedScheduledJob.class);
    private ApplicationContext context;
    private MetadataService service;

    @Override
    public void receive(Message message) {
        LOGGER.debug("Schedule task is started {}", ToStringBuilder.reflectionToString(message));

        loadContext("classpath*:spring/ifeed-*.xml");
        initBeans();

        if (service != null) {
            LOGGER.debug("Importing metadata");
            service.importMetadata();
        } else {
            LOGGER.error("Couldn't find bean for MetadataService");
        }
    }

    private void initBeans() {
        service = context.getBean(MetadataService.class);
    }

    private void loadContext(String configLocation) {
        LOGGER.debug("Loading spring context");
        if (context == null) {
            try {
                LOGGER.debug("Creating new application context using config location: {}", configLocation);
                context = new ClassPathXmlApplicationContext(configLocation);
                LOGGER.debug("Context created: {}", context);
            } catch (BeansException e) {
                e.printStackTrace();
                LOGGER.error("Context is null, failed to inialize: {}", e.getCause());
            }
        }
    }
}
