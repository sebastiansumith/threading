package com.suse.threads;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {

    private static Logger logger = Logger.getLogger(StampedLockDemo.class);

    private Map<String, String> synMap = new HashMap<String, String>();

    private StampedLock lock = new StampedLock();

    public void writeData(final String key, final String value) {
        long stamp = lock.writeLock();

        try {
            logger.info(Thread.currentThread().getName() + " writing for key " + key + " " + stamp);
            synMap.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
            logger.info(Thread.currentThread().getName() + " writing finished for key " + key + " " + stamp);
        }
    }

    public void readData(final String key) {
        long stamp = lock.readLock();

        try {
            Thread.sleep(1000);
            logger.info(Thread.currentThread().getName() + " reading for key " + key + " " + stamp);
            synMap.get(key);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            lock.unlockRead(stamp);
            logger.info(Thread.currentThread().getName() + " reading finished for key " + key + " " + stamp);
        }
    }


    public static void main(String[] args) {
        final StampedLockDemo demo = new StampedLockDemo();
        ExecutorService service = Executors.newFixedThreadPool(4);

        Runnable readable = () -> {
            demo.readData("test1");
        };

        Runnable writable = () -> {
            demo.writeData("test1", "value1");
        };

        Runnable readable1 = () -> {
            demo.readData("test2");
        };

        Runnable writable1 = () -> {
            demo.writeData("test2", "value2");
        };

        service.execute(writable);
        service.execute(readable);
        service.execute(writable1);
        service.execute(readable1);
        service.shutdown();

    }
}
