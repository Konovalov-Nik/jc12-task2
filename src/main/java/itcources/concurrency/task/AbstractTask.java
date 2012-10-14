package itcources.concurrency.task;


import itcources.concurrency.task.impl.ExecutorThread;

import java.util.concurrent.Callable;

public abstract class AbstractTask<T> implements Task<T> {
    protected Callable callableTask;
    protected volatile T value;
    protected volatile Exception ex;
    protected volatile boolean finished = false;
    protected volatile ExecutorThread executorThread;  //This is volatile for Lazy implementation, so that state could be viewed without delay.
    protected volatile boolean withException = false;

    protected AbstractTask(Callable callableTask) {
        this.callableTask = callableTask;
    }

    public Callable getCallableTask() {
        return callableTask;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public void setWithException(boolean withException) {
        this.withException = withException;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}