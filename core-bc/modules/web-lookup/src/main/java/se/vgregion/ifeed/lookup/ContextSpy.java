package se.vgregion.ifeed.lookup;

import se.vgregion.ifeed.tools.FeedDocumentIndexSupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ContextSpy implements ServletContextListener {

    // private static Executor executor = Executors.newFixedThreadPool(1);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("contextInitialized start");
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        final int firstRun = (int) (Math.random() * 24);
        // final int firstRun = 1;
        System.out.println("Starting in " + firstRun + " hours.");
        executor.scheduleAtFixedRate(() -> FeedDocumentIndexSupport.main(), firstRun, 24, TimeUnit.HOURS);
        System.out.println("contextInitialized end");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("contextDestroyed");
    }

}
