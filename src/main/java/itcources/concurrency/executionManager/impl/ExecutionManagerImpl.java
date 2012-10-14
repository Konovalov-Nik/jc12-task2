package itcources.concurrency.executionManager.impl;

import itcources.concurrency.executionManager.Context;
import itcources.concurrency.executionManager.ExecutionManager;

/**
 * @author Nikita Konovalov
 */
public class ExecutionManagerImpl implements ExecutionManager {
    private int defaultPoolSize = 3;
    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        return new ContextImpl(defaultPoolSize, callback, tasks);
    }

    public Context execute(int poolSize, Runnable callback, Runnable... tasks) {
        return new ContextImpl(poolSize, callback, tasks);
    }
}
