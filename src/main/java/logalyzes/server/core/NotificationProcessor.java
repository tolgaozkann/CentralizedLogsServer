package logalyzes.server.core;

import java.util.concurrent.*;

public class NotificationProcessor {
    private final BlockingQueue<Runnable> taskQueue;
    private final ExecutorService executorService;

    public NotificationProcessor(int threadPoolSize) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        startProcessing();
    }

    private void startProcessing() {
        for (int i = 0; i < ((ThreadPoolExecutor) executorService).getCorePoolSize(); i++) {
            executorService.submit(() -> {
                while (true) {
                    try {
                        Runnable task = taskQueue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void queueTask(Runnable task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
