package itcources.concurrency.executionManager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Nikita Konovalov
 */
public class SupervisorThread extends Thread {

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
    }

    @Override
    public synchronized void run() {
        for (Runnable task : tasks) {
            queue.add(task);
        }
        for (int i = 0; i < poolSize; i++) {
            ExecutorThread thread = new ExecutorThread(this);
            thread.setDaemon(true);
            thread.start();
            executors.add(thread);
        }
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
        if (isFinished()) {
            runCallbackOnce();
        }
    }

    public synchronized void registerFail() {
        failedTaskCount++;
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
            if (isFinished()) {
                runCallbackOnce();
            }
        }
    }

    private void runCallbackOnce() {
        if (!callbackCalled) {
            callback.run();
            shutdownExecutors(); // we dont need them anymore;
            callbackCalled = true;
        }
    }

    private void shutdownExecutors() {
        for (Thread executor : executors) {
            executor.interrupt();
        }
    }
}
