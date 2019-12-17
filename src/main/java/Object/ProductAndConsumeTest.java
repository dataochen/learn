package Object;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dataochen
 * @Description
 * @date: 2019/12/17 19:49
 */
public class ProductAndConsumeTest {
    private Integer MAX_LENGTH = 10;
    private Object[] container = new Object[MAX_LENGTH];
    private AtomicInteger count = new AtomicInteger(0);
    private Object lock = new Object();

    /**
     * 1.判断容器是否已满，如果已满，阻塞；否则存入
     */
    public void product(Object obj) {
        synchronized (lock) {
//            while 防止多个生产者情况；生产者释放锁，却被另一个生产者获取;如果用if 则第二个生产者或执行赋值导致数组越界
            while (count.get() == MAX_LENGTH) {
                try {
                    String name = Thread.currentThread().getName();
                    System.out.println(name + "生产者 开始阻塞");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            container[count.get()] = obj;
            int i = count.incrementAndGet();
            System.out.println("生产者+1，length=" + i);
            lock.notify();
        }
    }

    /**
     * 1.判断容器是否为空 如果为空，阻塞；否则拿走第一个
     */
    public void consume() {
        synchronized (lock) {
            //            while 防止多个消费者情况；消费者释放锁，却被另一个消费者获取;如果用if 则第二个消费者或执行赋值导致数组越界
            while (count.get() == 0) {
                try {
                    String name = Thread.currentThread().getName();
                    System.out.println(name + "消费者 开始阻塞");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Object o = container[0];
            int i = count.decrementAndGet();
            lock.notify();
            System.out.println("消费者-1，length=" + i);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        ProductAndConsumeTest productAndConsumeTest = new ProductAndConsumeTest();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> productAndConsumeTest.product(1)).start();
            new Thread(productAndConsumeTest::consume).start();
        }

    }
}
