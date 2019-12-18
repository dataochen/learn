package Lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author dataochen
 * @Description
 * @date: 2019/12/18 14:20
 */
public class ReentrantReadWriteLockTest {
    private static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    static ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
    static ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
    private static Object object = "init";

    public static void write() throws InterruptedException {
        String name = Thread.currentThread().getName();
        try {
            System.out.println(name + " 写锁开始");
            writeLock.lockInterruptibly();
            System.out.println(name + " 获取到写锁");
            writeLock.lockInterruptibly();
            object = name;
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                System.out.println(name + " 释放写锁");
                writeLock.unlock();
                writeLock.unlock();
            }
        }
    }

    public static void read() throws InterruptedException {
        String name = Thread.currentThread().getName();
        try {
            System.out.println(name + " 读锁开始");
            readLock.lockInterruptibly();
            System.out.println(name + " 获取到读锁");
            System.out.println(name + " " + object);
        } finally {
            System.out.println(name + " 释放读锁");
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    write();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    read();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
