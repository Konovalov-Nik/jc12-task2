package itcources.concurrency.task;

import itcources.concurrency.task.impl.LazyTask;
import itcources.concurrency.task.impl.SimpleTask;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Nikita Konovalov
 */
public class LazyTaskTest {
    @Test
    public void testHelloWorld() throws TaskExecutionException {
        AbstractTask<String> task = new LazyTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hello World";
            }
        });
        System.out.println(task.get());
    }

    @Test(expected = TaskExecutionException.class)
    public void testExecutionException() throws TaskExecutionException {
        AbstractTask<String> task = new LazyTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new Exception("Expected exception");
            }
        });
        System.out.println(task.get());
    }

    @Test
    public void testMultipleThreadGet() throws InterruptedException {
        final AbstractTask<String> task = new LazyTask<String>(new Callable<String>() {
            @Override
            public String call() {
                String ans = "";
                for (int i = 0; i < 50000; i++) {
                    ans = ans + "a"; //supposed to work slow enough to notice.
                }
                return ans;

                //return "Hello world";
            }
        });

        List<Thread> threads = new ArrayList<Thread>(20);
        for(int i = 0; i < 20; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Waiting for result.");
                        String result = task.get();
                        System.out.println("Got result.");
                    } catch (TaskExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

    }
}
