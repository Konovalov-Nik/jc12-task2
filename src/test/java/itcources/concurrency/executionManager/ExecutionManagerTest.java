package itcources.concurrency.executionManager;

import itcources.concurrency.executionManager.impl.ExecutionManagerImpl;
import org.junit.Test;

/**
 * @author Nikita Konovalov
 */
public class ExecutionManagerTest {
    @Test
    public void simpleTest() throws InterruptedException {
        ExecutionManager manager = new ExecutionManagerImpl();
        Runnable[] tasks = new Runnable[30];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new Runnable() {
                @Override
                public void run() {
                    String ans = "";
                    for (int i = 0; i < 15000; i++) {
                        ans = ans + ((i % 2 == 0) ? "a": "b"); //supposed to work slow enough to notice.
                    }
                }
            };
        }
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                System.out.println("Callback executed");
            }
        };

        Context context = manager.execute(callback, tasks);
        while (!context.isFinished()) {
            Thread.sleep(1000);
            System.out.println("completed: " + context.getCompletedTaskCount() + " failed: " + context.getFailedTaskCount() + " interrupted: " + context.getInterruptedTaskCount());
        }
    }
    @Test
    public void someFailTest() throws InterruptedException {
        ExecutionManager manager = new ExecutionManagerImpl();
        Runnable[] tasks = new Runnable[30];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new Runnable() {
                @Override
                public void run() {
                    String ans = "";
                    if (System.nanoTime() % 10 < 3) {
                        throw new RuntimeException("Bad task");
                    }
                    for (int i = 0; i < 15000; i++) {
                        ans = ans + ((i % 2 == 0) ? "a": "b"); //supposed to work slow enough to notice.
                    }
                }
            };
        }
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                System.out.println("Callback executed");
            }
        };

        Context context = manager.execute(callback, tasks);
        while (!context.isFinished()) {
            Thread.sleep(1000);
            System.out.println("completed: " + context.getCompletedTaskCount() + " failed: " + context.getFailedTaskCount() + " interrupted: " + context.getInterruptedTaskCount());
        }
    }


    @Test
    public void interruptAfterTwoSecondsTest() throws InterruptedException {
        ExecutionManager manager = new ExecutionManagerImpl();
        Runnable[] tasks = new Runnable[30];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new Runnable() {
                @Override
                public void run() {
                    String ans = "";
                    for (int i = 0; i < 15000; i++) {
                        ans = ans + ((i % 2 == 0) ? "a": "b"); //supposed to work slow enough to notice.
                    }
                }
            };
        }
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                System.out.println("Callback executed");
            }
        };

        Context context = manager.execute(callback, tasks);
        int cnt = 0;
        while (!context.isFinished()) {
            Thread.sleep(1000);
            cnt++;
            System.out.println("completed: " + context.getCompletedTaskCount() + " failed: " + context.getFailedTaskCount() + " interrupted: " + context.getInterruptedTaskCount());
            if (cnt == 2) {
                context.interrupt();
            }
        }
    }
}
