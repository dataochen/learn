package zookeeper;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author dataochen
 * @Description
 * @date: 2020/6/2 20:18
 */
public class ZkLock {
    private static volatile ZkLock instance;
    //    private static volatile SequentialLock sequentialLock;
    private ZkClient zkClient;
    private final String LOCKPATH = "/LOCK";

    private void init(String address) {
        if (zkClient == null) {
            synchronized (this) {
                if (zkClient == null) {
                    zkClient = new ZkClient(address, 10000, 10000, new SerializableSerializer());
                }
            }
        }
        return;
    }

    private ZkLock() {
    }

    private ZkLock(String address) {

    }

    public static ZkLock getInstance(String address) {
        if (instance == null) {
            synchronized (ZkLock.class) {
                if (instance == null) {
                    instance = new ZkLock();
                    instance.init(address);
                }
            }
        }
        return instance;
    }

    public boolean lock() {
        try {
            zkClient.createEphemeral(LOCKPATH);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 自旋获取锁
     * 可以指定超时时间 单位毫秒
     * 每10毫秒 自旋一次
     *
     * @param timeOut
     */
    public boolean tryLock(Long timeOut) {
        long time = timeOut;
        while (time >= 0) {
            time = time - 10;
            boolean lock = lock();
            if (lock) {
                return true;
            }
        }
        return false;
    }

    public boolean unLock() {
        boolean delete = zkClient.delete(LOCKPATH);
        return delete;
    }

    //    =================有序锁

    /**
     * 原理：抢最小节点
     * 1.在指定path下创建临时有序节点
     * 2.判断当前client的临时有序节点是否是path下最小的，如果是即可认为获取了锁
     * 3.如果不是，没有抢到锁，此时需要监听最小的节点，用于最小节点被删除（锁释放）激活当前client去抢锁（无需在创建节点，继续比较当前client的临时有序节点是否是path下最小的）
     * 注意：1.需要判断当前client是否已经创建过临时有序节点，如果是就不需要在创建临时有序节点了
     * 2.可通过临时有序节点改为临时无序节点 达到非公平锁的效果
     * 3.由于网络原因 client中断但session没有过期 导致client的节点没有删除，如果正好这个client是获取到锁的，
     */
    public class SequentialLock {
        private final String PATH = "/zkLock";
        private String ephemeralSequential;

        public SequentialLock() {
            if (!zkClient.exists(PATH)) {
                zkClient.createPersistent(PATH);
            }
        }

        /**
         *timeOut 单位 毫秒
         * @throws InterruptedException
         */
        public void tryLock(Long timeOut) {

            ephemeralSequential = zkClient.createEphemeralSequential(PATH + "/LOCK", new byte[0]);
            System.out.println(Thread.currentThread().getName() + "->创建节点：" + ephemeralSequential);
//            long timeOut = time;
//            long startTime = System.currentTimeMillis();
//            long endTime = System.currentTimeMillis();
//            if (endTime - startTime >= timeOut) {
//                throw new RuntimeException("timeOut time:" + timeOut);
//            }
            SequentialLock.this.lock(timeOut);

        }

        private boolean lock(Long timeOut) {
            String substring = ephemeralSequential.substring(8);
            List<String> children = zkClient.getChildren(PATH);
            children.sort(Comparator.naturalOrder());
            String minPath = children.get(0);
            if (substring.equals(minPath)) {
                System.out.println(Thread.currentThread().getName() + "->获取锁成功：" + ephemeralSequential);

                return true;
            }
            TreeSet<String> stringTreeset = new TreeSet<String>();
            stringTreeset.addAll(children);
            SortedSet<String> strings = stringTreeset.headSet(substring);
            if (CollectionUtils.isEmpty(strings)) {
//                已经是最新的节点
                throw new IllegalStateException("非法状态");
            }
            String path = PATH+"/" + strings.last();
//           注意：此处监听的是前一个path，;优点：解决惊群效应
            CountDownLatch countDownLatch = new CountDownLatch(1);
            IZkChildListener iZkChildListener = (parentPath, currentChilds) -> countDownLatch.countDown();
            if (!zkClient.exists(path)) {
                System.out.println(Thread.currentThread().getName() + "->PATH"+path+"已经不存在，获取锁成功：" + ephemeralSequential);

                return true;
            }
            zkClient.subscribeChildChanges(path, iZkChildListener);
            //            异步计时器 超时解除countDownLatch todo
            try {
                boolean await = countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
                if (!await) {
                    System.out.println("超时 timeOut="+timeOut);
                    return await;
                }
            } catch (InterruptedException e) {
                return false;
            }
            zkClient.unsubscribeChildChanges(path, iZkChildListener);
            System.out.println(Thread.currentThread().getName() + "->成功订阅排序到次线程，获取锁成功：" + ephemeralSequential);

            return true;
        }

        public void unLock() {
            zkClient.delete(ephemeralSequential);
            System.out.println(Thread.currentThread().getName() + "->删除节点" + ephemeralSequential);

        }
    }

    public static SequentialLock getSequentialLockInstance(String address) {
        ZkLock instance = ZkLock.getInstance(address);
        ZkLock.instance.init(address);
        ZkLock.SequentialLock sequentialLock = instance.new SequentialLock();
        return sequentialLock;
    }

}
