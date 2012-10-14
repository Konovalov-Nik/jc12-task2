package itcources.concurrency.task.impl;

import itcources.concurrency.task.AbstractTask;

/**
* @author Nikita Konovalov
*/
public class ExecutorThread extends Thread {
    private AbstractTask task;

    ExecutorThread(AbstractTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        try {
            task.setValue(task.getCallableTask().call());
        } catch (Exception e) {
            task.setEx(e);
            task.setWithException(true);
        } finally {
            task.setFinished(true);
        }
    }
}
