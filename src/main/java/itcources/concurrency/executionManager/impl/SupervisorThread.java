package itcources.concurrency.executionManager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Nikita Konovalov
 */
public class SupervisorThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(SupervisorThread.class);

    private List<Thread> executors = new ArrayList<Thread>();
    private Runnable callback;
    private List<Runnable> tasks;
    private int poolSize;
    private int taskCount;


    private volatile int completedTaskCount = 0;
    private volatile int failedTaskCount = 0;
    private volatile int interruptedTaskCount = 0;

    private final BlockingQueue<Runnable> queue;
    private volatile boolean interrupted = false;
    private volatile boolean callbackCalled = false;

    public SupervisorThread(Runnable callback, List<Runnable> tasks, int poolSize) {
        this.callback = callback;
        this.tasks = tasks;
        this.poolSize = poolSize;
        this.taskCount = tasks.size();
        this.queue = new LinkedBlockingQueue<Runnable>();
        LOG.info("Supervisor created.");
    }

    @Override
    public synchronized void run() {
        LOG.info("Supervisor started.");
        for (Runnable task : tasks) {
            queue.add(task);
        }
        LOG.debug("Tasks added to queue.");
        for (int i = 0; i < poolSize; i++) {
            ExecutorThread thread = new ExecutorThread(this);
            thread.setDaemon(true);
            thread.start();
            LOG.trace("Executor started.");
            executors.add(thread);
        }
        LOG.debug("Executors started.");
    }

    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    public int getFailedTaskCount() {
        return failedTaskCount;
    }

    public int getInterruptedTaskCount() {
        return interruptedTaskCount;
    }

    public boolean isFinished() {
        return (completedTaskCount + failedTaskCount + interruptedTaskCount) == taskCount;
    }

    public BlockingQueue<Runnable> getQueue() {
        return queue;
    }

    public synchronized void registerComplete() {
        completedTaskCount++;
        LOG.debug("Task finished.");
        if (isFinished()) {
            runCallbackOnce();
        }
    }

    public synchronized void registerFail() {
        failedTaskCount++;
        LOG.debug("Task failed.");
        if (isFinished()) {
            runCallbackOnce();
        }
    }

    public synchronized void doInterrupt() {
        if (!interrupted) {
            ArrayList<Runnable> pending = new ArrayList<Runnable>();
            queue.drainTo(pending);
            interruptedTaskCount = pending.size();
            interrupted = true;
            LOG.debug("Execution interrupted.");
            if (isFinished()) {
                runCallbackOnce();
            }
        }
    }

    private void runCallbackOnce() {
        if (!callbackCalled) {
            LOG.info("Callback started");
            callback.run();
            shutdownExecutors(); // we dont need them anymore;
            callbackCalled = true;
        }
    }

    private void shutdownExecutors() {
        for (Thread executor : executors) {
            executor.interrupt();
        }
        LOG.debug("Executors stopped.");
    }
}
