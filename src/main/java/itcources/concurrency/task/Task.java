package itcources.concurrency.task;

/**
 * @author Nikita Konovalov
 */
public interface Task<T> {
    T get() throws TaskExecutionException;
}
