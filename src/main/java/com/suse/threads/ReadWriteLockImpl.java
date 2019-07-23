package com.suse.threads;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

public class ReadWriteLockImpl {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private Map<String, String> synchMap = new HashMap<String, String>();
    final static Logger logger = Logger.getLogger(ReadWriteLockImpl.class);

    public ReadWriteLockImpl(){

    }

    public void getData(final String key){
        this.readLock.lock();
        try{
            logger.info(Thread.currentThread().getName() + " reading: "+key);
            synchMap.get(key);
        }finally {
            readLock.unlock();
        }
        logger.info(Thread.currentThread().getName() + " reading finished: "+key);
    }

    public void removeData(final String key){
        this.writeLock.lock();
        try {
            this.synchMap.remove(key);
        }finally {
            writeLock.unlock();
        }
    }

    public void writeData(final String key, final String value) throws InterruptedException {
        this.writeLock.lock();
        try {
            logger.info(Thread.currentThread().getName() + " writing: "+key);
            this.synchMap.put(key, value);
            sleep(1000);
        }finally {
            writeLock.unlock();
        }
        logger.info(Thread.currentThread().getName() + "finished writing: "+key);
    }

    public static void main(String args []){

        final int threadCount = 10;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        ReadWriteLockImpl impl = new ReadWriteLockImpl();

        service.execute(new Thread(new Writer(impl), "Writer1"));
        service.execute(new Thread(new Reader(impl), "Reader1"));
        service.execute(new Thread(new Reader(impl), "Reader2"));
        service.execute(new Thread(new Writer(impl), "Writer2"));
        service.execute(new Thread(new Reader(impl), "Reader3"));
        service.execute(new Thread(new Reader(impl), "Reader4"));
        service.execute(new Thread(new Writer(impl), "Writer3"));
        service.execute(new Thread(new Reader(impl), "Reader5"));
        service.execute(new Thread(new Reader(impl), "Reader6"));
        service.execute(new Thread(new Writer(impl), "Writer5"));

        service.shutdown();

    }

    private static class Writer implements Runnable{

        ReadWriteLockImpl obj;

        public Writer(ReadWriteLockImpl obj){
           this.obj = obj;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        public void run() {
            for(int i=0; i<10; i++){
                try {
                    this.obj.writeData("key"+i, "value"+i );
                } catch (InterruptedException e) {
                    System.out.println(e.getStackTrace());
                }
            }

        }
    }


    private static class Reader implements Runnable{

        ReadWriteLockImpl obj;

        public Reader(ReadWriteLockImpl obj){
            this.obj = obj;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        public void run() {
            for(int i=0; i<10; i++) {
                this.obj.getData("key" + i);
            }

        }
    }



}
