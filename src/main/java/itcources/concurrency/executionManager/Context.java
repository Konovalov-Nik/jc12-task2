package itcources.concurrency.executionManager;

/**
 * @author Nikita Konovalov
 */
public interface Context {
    int getCompletedTaskCount();
    int getFailedTaskCount();
    int getInterruptedTaskCount();
    void interrupt();
    boolean isFinished();
}
