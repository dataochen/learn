package zookeeper;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dataochen
 * @Description 有序读写锁 不可重入
 * Note:存在写饥饿情况；有序公平锁
 * 读写锁概念：读读共享；读写，写读，写写互斥。
 * 原理解读：
 * 1.通过创建临时有序的读写节点
 * 2.根据不同的读写规则来指定是否获取锁
 * 例子：假如某时刻 存在如下节点
 * /zk/readWriteLock/read_01
 * /zk/readWriteLock/write_02
 * /zk/readWriteLock/read_03
 * /zk/readWriteLock/read_04
 * /zk/readWriteLock/write_05
 * /zk/readWriteLock/read_06
 * 读锁：如果/zk/readWriteLock中不存在z节点（含有write的子节点并且序列号小于当前线程创建的节点序列号j,此节点是里j最近的小序列号节点），则可获取锁；否则需要监听
 * z节点，当节点消失后重新判断是否可以获取锁。如上例,read_01可以获取读锁，read_03，read_04不可获取读锁,read_06也不可获取读锁
 * 写锁：如果/zk/readWriteLock中不存在X节点（序列号小于当前线程创建的节点序列号j，无论read还是write，此节点是里j最近的小序列号节点）则可获取锁；否则需要监听
 * X节点，当节点消失后重新判断是否可以获取锁。
 * @date: 2020/7/6 9:59
 */
public class ZkReadWriteLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkReadWriteLock.class);

    private static final String path = "/zk/readWriteLock";
    private static volatile ZkReadWriteLock instance;
    private ZkClient zkClient;
    private Object lock;

    private ZkReadWriteLock(String address) {
        init(address);
    }

    public static ZkReadWriteLock getInstance(String address) {
        if (instance == null) {
            synchronized (ZkReadWriteLock.class) {
                if (instance == null) {
                    instance = new ZkReadWriteLock(address);
                }
            }
        }
        return instance;
    }

    private void init(String address) {
        zkClient = new ZkClient(address, 10000, 10000, new SerializableSerializer());
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path, true);
        }
    }

    /**
     * 如果/zk/readWriteLock中不存在z节点（含有write的子节点并且序列号小于当前线程创建的节点序列号j,此节点是里j最近的小序列号节点），则可获取锁；否则需要监听
     * z节点，当节点消失后重新判断是否可以获取锁。
     *
     * @return
     */
    public boolean readLock(long timeOut) {
        String fullPath = path + "/read_";
        String ephemeralSequential = zkClient.createEphemeralSequential(fullPath, null);
//        ====
        List<String> children = zkClient.getChildren(path);
        if (null == children || children.size() == 0) {
            LOGGER.info("无任何子节点，获取读锁成功");
            return true;
        }
        List<String> collect = children.stream().filter(path -> path.contains("write")).map(this::getNum).collect(Collectors.toList());
        if (null == collect || collect.size() == 0) {
            LOGGER.info("无任何子节点包含write，获取读锁成功");
            return true;
        }
        TreeSet<String> strings = new TreeSet<>();
        strings.addAll(collect);
        SortedSet<String> lessPathSet = strings.headSet(getNum(ephemeralSequential));
        if (0 == lessPathSet.size()) {
            LOGGER.info("无小于当前线程序列号的节点，获取读锁成功");
            return true;
        }
//        z节点 含有write的子节点并且序列号小于当前线程创建的节点序列号j
        String last1 = lessPathSet.last();
        String last = children.stream().filter(x -> x.contains(last1)).findFirst().get();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        IZkChildListener iZkChildListener = (parentPath, currentChilds) -> {
            countDownLatch.countDown();
        };
        zkClient.subscribeChildChanges(path+"/"+last, iZkChildListener);
//受阻
        //            异步计时器 超时解除countDownLatch
        try {
            boolean await = countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
            if (!await) {
                System.out.println("超时 timeOut=" + timeOut);
                return await;
            }
        } catch (InterruptedException e) {
            return false;
        }
        zkClient.unsubscribeChildChanges(path+"/"+last, iZkChildListener);
        return true;
    }

    /**
     * 如果/zk/readWriteLock中不存在X节点（序列号小于当前线程创建的节点序列号j，无论read还是write，此节点是里j最近的小序列号节点）则可获取锁；否则需要监听
     * X节点，当节点消失后重新判断是否可以获取锁。
     */
    public boolean writeLock(long timeOut) {
        String fullPath = path + "/write_";
        String ephemeralSequential = zkClient.createEphemeralSequential(fullPath, null);
//        ====
        List<String> children = zkClient.getChildren(path);
        if (null == children || children.size() == 0) {
            LOGGER.info("无任何子节点，获取写锁成功");
            return true;
        }
        List<String> collect = children.stream().map(this::getNum).collect(Collectors.toList());
        TreeSet<String> strings = new TreeSet<>();
        strings.addAll(collect);
        SortedSet<String> lessPathSet = strings.headSet(getNum(ephemeralSequential));
        if (0 == lessPathSet.size()) {
            LOGGER.info("无小于当前线程序列号的节点，获取写锁成功");
            return true;
        }
//        z节点 含有write的子节点并且序列号小于当前线程创建的节点序列号j
        String last1 = lessPathSet.last();
        String last = children.stream().filter(x -> x.contains(last1)).findFirst().get();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        IZkChildListener iZkChildListener = (parentPath, currentChilds) -> {
            countDownLatch.countDown();
        };
        zkClient.subscribeChildChanges(path+"/"+last, iZkChildListener);
//受阻
        //            异步计时器 超时解除countDownLatch
        try {
            boolean await = countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
            if (!await) {
                System.out.println("超时 timeOut=" + timeOut);
                return await;
            }
        } catch (InterruptedException e) {
            return false;
        }
        zkClient.unsubscribeChildChanges(path+"/"+last, iZkChildListener);
        return true;
    }

    public void unReadLock() {
        // TODO: 2020/7/6  
    }

    private String getNum(String currentPath) {
        if (currentPath.contains(path)) {
            currentPath = currentPath.substring(18);
        }
        if (currentPath.contains("read_")) {
            return currentPath.substring(5);
        } else if (currentPath.contains("write_")) {
            return currentPath.substring(6);
        } else {
            return currentPath;
        }
    }
    

    public static void main(String[] args) {
        ZkReadWriteLock lock = ZkReadWriteLock.getInstance("127.0.0.1");
        new Thread(() -> {
            lock.readLock(3000);
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName());
        }).start();
        new Thread(() -> {
            lock.readLock(3000);
            System.out.println(Thread.currentThread().getName());
        }).start();
        new Thread(() -> {
            lock.writeLock(3000);
            System.out.println(Thread.currentThread().getName());
        }).start();

    }

}
