package nl.juraji.biliomi.utility.factories.concurrent;

import nl.juraji.biliomi.utility.exceptions.RejectedTimerExecutionHandler;

import java.util.concurrent.*;

public final class ThreadPools {
    private ThreadPools() {
    }

    public static ExecutorService newExecutorService(int maxThreads, String name) {
        return new ThreadPoolExecutor(
                0,
                maxThreads,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(),
                DefaultThreadFactory.newFactory(name, maxThreads == 1),
                new RejectedTimerExecutionHandler()
        );
    }

    public static ScheduledExecutorService newScheduledExecutorService(String name) {
        return newScheduledExecutorService(1, name);
    }

    public static ScheduledExecutorService newScheduledExecutorService(int poolSize, String name) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(poolSize);
        executor.setRejectedExecutionHandler(new RejectedTimerExecutionHandler());
        executor.setRemoveOnCancelPolicy(true);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        executor.setThreadFactory(DefaultThreadFactory.newFactory(name, poolSize == 1));
        return executor;
    }

    public static ExecutorService newSingleThreadExecutor(String name) {
        return Executors.newSingleThreadExecutor(DefaultThreadFactory.newFactory(name, true));
    }
}
