package itcources.concurrency.task.impl;

import itcources.concurrency.task.AbstractTask;
import itcources.concurrency.task.Task;
import itcources.concurrency.task.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author Nikita Konovalov
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public class LazyTask<T> extends AbstractTask<T> implements Task<T> {

    private static final Logger LOG = LoggerFactory.getLogger(LazyTask.class);

    public LazyTask(Callable<? extends T> callableTask) {
        super(callableTask);
        executorThread = new ExecutorThread(this);
    }

    @Override
    public T get() throws TaskExecutionException {
        while (true) {
            if (finished) {
                if (withException) {
                    LOG.debug("Task failed with exception.", ex);
                    throw new TaskExecutionException("Task failed with exception.", ex);
                } else {
                    return value;
                }
            } else {
                try {
                    synchronized (executorThread) {
                        if (executorThread.getState() == Thread.State.NEW) {
                            executorThread.start();
                            LOG.info("New thread started for " + callableTask);
                        }
                    }
                    executorThread.join();
                    LOG.debug("Task finished.");

                } catch (InterruptedException e) {
                    LOG.debug("Task was interrupted.", e);
                    throw new TaskExecutionException("Task was interrupted.", e);
                }
            }

        }
    }

}
