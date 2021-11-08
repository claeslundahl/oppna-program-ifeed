package se.vgregion.ifeed.lookup;

import se.vgregion.ifeed.tools.FeedDocumentIndexSupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ContextSpy implements ServletContextListener {

    // private static Executor executor = Executors.newFixedThreadPool(1);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("contextInitialized start");
/*        executor.(() -> FeedDocumentIndexSupport.main(), 1, TimeUnit.MINUTES);*/
        /*final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);
        executor.schedule(() -> FeedDocumentIndexSupport.main(), 1, TimeUnit.MINUTES);
        System.out.println("contextInitialized end");*/
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("contextDestroyed");
    }

}
