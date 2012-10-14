package itcources.concurrency.semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Nikita Konovalov
 */
public class Semaphore {
    private final Logger LOG = LoggerFactory.getLogger(Semaphore.class);
    private static int lastId = 0;

    private int id;
    private final Object acqWait = new Object();
    private final Object relWait = new Object();
    private int initValue;
    private int value;

    public Semaphore(int initValue) {
        if (initValue <= 0) {
            throw new IllegalArgumentException("initValue should be positive. Found" + initValue + ";");
        }
        this.initValue = initValue;
        value = initValue;
        id = lastId++;
    }

    public void acquire() throws InterruptedException {
        acquire(1);
    }

    public void acquire(int number) throws InterruptedException {
        if (number > initValue) {
            throw new IllegalArgumentException("Cant acquire more than initValue.InitValue = " + initValue + "; number = " + number + ";");
        }
        LOG.debug("Semaphore " + id + " aquire " + number);
        if (number <= 0) {
            return;
        }
        synchronized (this) {
            while (true) {
                int free = value;
                int takeNow = Math.min(free, number);

                value -= takeNow;
                number -= takeNow;
                LOG.debug("Semaphore " + id + " takes " + takeNow + " left " + number);

                synchronized (relWait) {
                    relWait.notifyAll();
                }

                if (number == 0) {
                    break;
                } else {
                    synchronized (acqWait) {
                        acqWait.wait();
                    }
                }


            }
        }
    }

    public boolean tryAcquire(int number, long timeToWait, TimeUnit timeUnit) throws InterruptedException {

        if (number > initValue) {
            throw new IllegalArgumentException("Cant acquire more than initValue.InitValue = " + initValue + "; number = " + number + ";");
        }
        LOG.debug("Semaphore " + id + " aquire " + number);
        if (number <= 0) {
            return false;
        }
        synchronized (this) {
            while (true) {
                int free = value;
                if (free >= number) {
                    value -= number;
                    LOG.debug("Semaphore " + id + " takes " + number);

                    synchronized (relWait) {
                        relWait.notifyAll();
                    }
                    return true;
                } else {
                    synchronized (acqWait) {
                        long start = System.currentTimeMillis();
                        acqWait.wait(timeUnit.toMillis(timeToWait));
                        long wake = System.currentTimeMillis();
                        timeToWait -= wake - start;
                        if (timeToWait <= 0) {
                            LOG.debug("Semaphore " + id + " timed out on tryAcquire.");
                            return false;
                        }
                    }
                }

            }
        }
    }

    public boolean tryAcquire(long timeToWait, TimeUnit timeUnit) throws InterruptedException {
        return tryAcquire(1, timeToWait, timeUnit);
    }

    public boolean tryAcquire(int number) throws InterruptedException {
        return tryAcquire(number, 0, TimeUnit.MILLISECONDS);
    }

    public boolean tryAcquire() throws InterruptedException {
        return tryAcquire(1);
    }


    public void release() throws InterruptedException{
        release(1);
    }

    public void release(int number) throws InterruptedException{
        if (number > initValue) {
            throw new IllegalArgumentException("Cant release more than initValue.InitValue = " + initValue + "; number = " + number + ";");
        }
        if (number <= 0) {
            return;
        }
        LOG.debug("Semaphore " + id + " release " + number);

        synchronized (this) {
            while (true) {
                int possibleToRelease = initValue - value;
                int releaseNow = Math.min(possibleToRelease, number);

                value += releaseNow;
                number -= releaseNow;
                LOG.debug("Semaphore " + id + " releases " + releaseNow + " left " + number);

                if (number == 0) {
                    break;
                } else {
                    synchronized (relWait) {
                        relWait.wait();
                    }
                }
            }
        }
    }


}