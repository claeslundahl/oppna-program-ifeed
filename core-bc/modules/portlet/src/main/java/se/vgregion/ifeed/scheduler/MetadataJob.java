package se.vgregion.ifeed.scheduler;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.vgregion.ifeed.service.metadata.MetadataService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MetadataJob implements ServletContextListener {

    private static MetadataService metadataService;

    private static DailyRun dailyRun;

    public MetadataJob() {
        super();
        System.out.println("MetadataJob is created!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) {
        System.out.println("ServletContextListener destroyed");
        if (dailyRun == null) {
            dailyRun.stop();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent context) {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(() -> todo(), 200, TimeUnit.SECONDS);
        if (dailyRun == null) {
            dailyRun = new DailyRun(() -> todo());
            dailyRun.startExecutionAt(0, 0, 0);
        }

        loadContext("classpath*:spring/ifeed-*.xml");
    }

    void todo() {
        if (metadataService != null) {
            System.out.println("Importing metadata");
            metadataService.importMetadata();
        } else {
            System.out.println("Couldn't find bean for MetadataService");
        }
    }

    protected void loadContext(final String configLocation) {
        try {
            // LOGGER.debug("Creating new application context using config " + "location: {}", configLocation);
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configLocation);
            MetadataService service = context.getBean(MetadataService.class);
            if (service == null)
                System.out.println("Service is null!");
            else
                System.out.println("Service is not null!");
            setMetadataService(service);
            //LOGGER.debug("Context created: {}", context);
        } catch (BeansException e) {
            e.printStackTrace();
            //LOGGER.error("Context is null, failed to inialize: {}", e.getCause());
        }
    }


    public MetadataService getMetadataService() {
        return MetadataJob.metadataService;
    }

    public void setMetadataService(MetadataService metadataService) {
        MetadataJob.metadataService = metadataService;
        System.out.println("After setting the metadataService ref: " + metadataService);
    }

}