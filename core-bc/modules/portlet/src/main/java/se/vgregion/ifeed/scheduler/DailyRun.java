package se.vgregion.ifeed.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyRun {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private Task currentTask;

    public DailyRun(Task task) {
        this.currentTask = task;
    }

    public void startExecutionAt(int targetHour, int targetMin, int targetSec) {
        Runnable taskWrapper = () -> {
            currentTask.execute();
        };
        executor.scheduleAtFixedRate(taskWrapper, 0, 24, TimeUnit.HOURS);
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

}