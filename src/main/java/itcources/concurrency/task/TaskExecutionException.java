package itcources.concurrency.task;

/**
 * @author Nikita Konovalov
 */
public class TaskExecutionException extends Exception {
    public TaskExecutionException(String message) {
        super(message);
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
