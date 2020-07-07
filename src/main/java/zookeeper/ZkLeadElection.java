package zookeeper;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author dataochen
 * @Description 选主
 * 节点最小的为leader
 * leader消失后 自动选举此时刻最小的有效节点
 * @date: 2020/7/7 16:11
 */
public class ZkLeadElection {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkLeadElection.class);

    private static final String path = "/zk/election";
    private static volatile ZkLeadElection instance;
    private ZkClient zkClient;
    private static final String leaderPath = "/zk/leader";

    private ZkLeadElection(String address) {
        init(address);
    }

    public static ZkLeadElection getInstance(String address) {
        if (instance == null) {
            synchronized (ZkLeadElection.class) {
                if (instance == null) {
                    instance = new ZkLeadElection(address);
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
        if (!zkClient.exists(leaderPath)) {
            zkClient.createPersistent(leaderPath, true);
        }
    }

    /**
     * 选举领导者
     * leader
     */
    public void electionLeader(String nickName) {
        String ephemeralSequential = zkClient.createEphemeralSequential(path + "/zt", null);
        System.out.println(ephemeralSequential);
        logicCommon(ephemeralSequential, nickName);

    }

    private void logicCommon(String ephemeralSequential, String nickName) {
        List<String> children = zkClient.getChildren(path);
        if (null == children || children.size() == 0) {
            zkClient.writeData(leaderPath, nickName);
            return;
        }
        TreeSet<String> strings = new TreeSet<>();
        strings.addAll(children);
        SortedSet<String> lessPathSet = strings.headSet(ephemeralSequential.substring(path.length() + 1));
        if (0 == lessPathSet.size()) {
            LOGGER.info("无小于当前线程序列号的节点，此节点为领导者" + ephemeralSequential + ";" + nickName);
            zkClient.writeData(leaderPath, nickName);
            return;
        }
        String last1 = lessPathSet.last();
        String last = children.stream().filter(x -> x.contains(last1)).findFirst().get();
        IZkChildListener iZkChildListener = (parentPath, currentChilds) -> {
//            System.out.println("2收到通知，获取通知的父节点" + parentPath);
//            //            需要判断是否是最小子节点 如果不是 继续监听比当前子节点小但是最靠近的子节点；例子：01,02,03；03监听了02子节点，
//// 但是如果02客户端关闭会导致03收到通知，所以必须再次判断03是不是最小节点。如果不是监听01节点（02已无效）
//            List<String> children2 = zkClient.getChildren(path);
//            if (null == children2 || children2.size() == 0) {
//                LOGGER.info("2无任何子节点，此节点为领导者" + nickName);
//                zkClient.writeData(leaderPath,nickName);
//
//                return;
//            }
//            TreeSet<String> strings2 = new TreeSet<>();
//            strings2.addAll(children2);
//            SortedSet<String> lessPathSet2 = strings2.headSet(ephemeralSequential.substring(path.length()+1));
//            if (0 == lessPathSet2.size()) {
//                LOGGER.info("2无小于当前线程序列号的节点，此节点为领导者" + nickName);
//                zkClient.writeData(leaderPath,nickName);
//
//                return;
//            }
////        z节点 含有write的子节点并且序列号小于当前线程创建的节点序列号j
//            String last12 = lessPathSet2.last();
//            String last2 = children2.stream().filter(x -> x.contains(last12)).findFirst().get();
//            LOGGER.warn("2继续监听上一个有效节点"+last2);
//            zkClient.exists(path + "/" + last2);
            logicCommon(ephemeralSequential, nickName);
        };
        System.out.println("监听last" + last);
        zkClient.subscribeChildChanges(path + "/" + last, iZkChildListener);
    }

    public static void main(String[] args) throws IOException {
        ZkLeadElection zk = ZkLeadElection.getInstance("127.0.0.1");
        double random = Math.random();
        System.out.println(random);
        zk.electionLeader(Thread.currentThread().getName() + random);

//        new Thread(()->{zk.electionLeader("线程1");}).start();

//        new Thread(()->{zk.electionLeader("线程2");}).start();
        Object leader = zk.zkClient.readData(leaderPath);
        System.out.println("领导节点是" + leader);
        System.in.read();
    }
}
