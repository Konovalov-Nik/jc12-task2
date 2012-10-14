package itcources.concurrency.task;

import itcources.concurrency.task.impl.SimpleTask;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

/**
 * @author Nikita Konovalov
 */
public class SimpleTaskTest {
    @Test
    public void testHelloWorld() throws TaskExecutionException {
        AbstractTask<String> task = new SimpleTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hello World";
            }
        });
        System.out.println(task.get());
    }

    @Test(expected = TaskExecutionException.class)
    public void testExecutionException() throws TaskExecutionException {
        AbstractTask<String> task = new SimpleTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new Exception("Expected exception");
            }
        });
        System.out.println(task.get());
    }

    @Test(expected = TaskExecutionException.class)
    public void testInterrupted() throws NoSuchFieldException, IllegalAccessException, TaskExecutionException, InterruptedException {
        AbstractTask<String> task = new SimpleTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
               Thread.sleep(10000);
                return "Hello World";
            }
        });

        Class clazz = AbstractTask.class;
        Field executor = clazz.getDeclaredField("executorThread");
        executor.setAccessible(true);
        ((Thread)executor.get(task)).interrupt();
        Thread.sleep(3000);

        System.out.println(task.get());
    }
}
