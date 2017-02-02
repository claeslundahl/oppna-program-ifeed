package se.vgregion.ifeed.jobs;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MidnightRun {

    private static final Logger LOGGER = LoggerFactory.getLogger(MidnightRun.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final long day = (24 * 60 * 60 * 1000);

    private Task currentTask;
    private static Date nextRun;
    private static Date previousRun;
    private static String lastErrorIfAny;

    public MidnightRun(Task task) {
        this.currentTask = task;
    }

    public void startTimer() {
        long timeToMidnight = getTimeToNextRun();

        Runnable taskWrapper = () -> run();
        executor.scheduleAtFixedRate(taskWrapper, timeToMidnight, day, TimeUnit.MILLISECONDS);
    }

    public long getTimeToNextRun() {
        long now = System.currentTimeMillis();
        long timeToMidnight = day - (now % (day));
        return timeToMidnight;
    }

    public void run() {
        previousRun = new Date();
        try {
            currentTask.execute();
            lastErrorIfAny = "";
        } catch (Exception e) {
            lastErrorIfAny = ExceptionUtils.getStackTrace(e);
            LOGGER.error("Error when running delayed task.", e);
        }
    }

    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public interface Task {
        void execute();
    }

    public static Date getNextRun() {
        return nextRun;
    }

    public static Date getPreviousRun() {
        return previousRun;
    }

    public static String getLastErrorIfAny() {
        return lastErrorIfAny;
    }

}