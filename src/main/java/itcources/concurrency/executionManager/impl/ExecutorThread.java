package itcources.concurrency.executionManager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikita Konovalov
 */
public class ExecutorThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutorThread.class);

    private SupervisorThread supervisor;
    public ExecutorThread(SupervisorThread supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public void run() {
        while (true) {
            Runnable task;
            try {
                task = supervisor.getQueue().take();
                LOG.trace("Task taken from queue.");
            } catch (InterruptedException e) {
                LOG.debug("Executor was interrupted.");
                break; // supervisor has interrupted this thread;
            }
            if (task != null) {
                try {
                    task.run();
                    LOG.trace("Task finished.");
                    supervisor.registerComplete();
                } catch (Exception e) {
                    LOG.trace("Task failed.", e);
                    supervisor.registerFail();
                }
            }
        }
    }
}
