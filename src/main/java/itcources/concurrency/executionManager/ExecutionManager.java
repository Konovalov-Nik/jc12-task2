package itcources.concurrency.executionManager;

/**
 * @author Nikita Konovalov
 */
public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}
