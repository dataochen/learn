package Lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dataochen
 * @Description
 * @date: 2019/12/18 9:59
 */
public class ReentrantLockTest {
    private static ReentrantLock lock = new ReentrantLock(false);
    private static int i = 0;

    static class Test {

        public void out() {
            String name = Thread.currentThread().getName();
            System.out.println(name + " " + i++);
        }
    }

    public static void main(String[] args) {
        Test test = new Test();

//        testReentrantLockNum();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                lock.lock();
                test.out();
                lock.unlock();
            }).start();
        }

    }

    /**
     * 测试 可重入锁的最大深度
     * 结果：最大数为Integer.MAX_VALUE
     */
    public static void testReentrantLockNum() {
        while (true) {
            lock.lock();
        }
    }
}
