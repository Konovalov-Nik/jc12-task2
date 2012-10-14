package itcources.concurrency.executionManager.impl;

import itcources.concurrency.executionManager.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Nikita Konovalov
 */
public class ContextImpl implements Context {

    private SupervisorThread supervisor;

    public ContextImpl(int poolSize, Runnable callback, Runnable... tasks) {
        supervisor = new SupervisorThread(callback ,Arrays.asList(tasks), poolSize);
        supervisor.start();
    }
    @Override
    public int getCompletedTaskCount() {
        return supervisor.getCompletedTaskCount();
    }

    @Override
    public int getFailedTaskCount() {
        return supervisor.getFailedTaskCount();
    }

    @Override
    public int getInterruptedTaskCount() {
        return supervisor.getInterruptedTaskCount();
    }

    @Override
    public void interrupt() {
        supervisor.doInterrupt();
    }

    @Override
    public boolean isFinished() {
        return supervisor.isFinished();
    }
}
