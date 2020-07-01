package zookeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author dataochen
 * @Description
 * @date: 2020/6/2 20:36
 */
public class ZkLockTest {
    static int j = 0;

    public static void main(String[] args) {
        SequentialLockTest();
    }

    public static void cc() {
        ZkLock lock = ZkLock.getInstance("127.0.0.1");
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                boolean b = lock.tryLock(100L);
                if (!b) {
                    System.out.println("====");
                    return;
                }
//            System.out.println("ThreadOne"+i);
                int k = j;
                j = k + 1;
                System.out.println(j);
//                lock.unLock();
            }
        });
        thread.start();
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                boolean b = lock.tryLock(100L);
                if (!b) {
                    System.out.println("====");
                }
                int k = j;
                j = k + 1;
                System.out.println(j);
//                System.out.println("ThreadTwo"+i);
                lock.unLock();

            }
        });
//        thread2.start();
    }

    //    public static void SequentialLockTest() {
//        ZkLock.SequentialLock sequentialLockInstance = ZkLock.getSequentialLockInstance("127.0.0.1");
//        Thread thread1 = new Thread(() -> {
//            for (int i = 0; i < 1000; i++) {
//                sequentialLockInstance.tryLock();
//                int k=j;
//                j=k+1;
//                System.out.println(j);
//                sequentialLockInstance.unLock();
//            }
//        });     Thread thread2 = new Thread(() -> {
//            for (int i = 0; i < 1000; i++) {
//                sequentialLockInstance.tryLock();
//                int k=j;
//                j=k+1;
//                System.out.println(j);
//                sequentialLockInstance.unLock();
//            }
//        });
//        thread1.start();
//        thread2.start();
//    }
    public static void SequentialLockTest() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    ZkLock.SequentialLock sequentialLockInstance = ZkLock.getSequentialLockInstance("127.0.0.1");
                    countDownLatch.await();
                    sequentialLockInstance.tryLock(5000L);
                    TimeUnit.SECONDS.sleep(5);
                    sequentialLockInstance.unLock();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }, "thread--" + i).start();
            countDownLatch.countDown();
        }
    }
}
