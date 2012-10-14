package itcources.concurrency.executionManager.impl;

/**
 * @author Nikita Konovalov
 */
public class ExecutorThread extends Thread {
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
            } catch (InterruptedException e) {
                break; // supervisor has interrupted this thread;
            }
            if (task != null) {
                try {
                    task.run();
                    supervisor.registerComplete();
                } catch (Exception e) {
                    supervisor.registerFail();
                }
            }
        }
    }
}
