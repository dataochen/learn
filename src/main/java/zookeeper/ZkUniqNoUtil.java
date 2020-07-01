package zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.springframework.util.StringUtils;

/**
 * @author dataochen
 * @Description zk id生成器
 * @date: 2020/6/3 10:03
 */
public class ZkUniqNoUtil {
    private static volatile ZkUniqNoUtil instance;
    private ZkClient zkClient;

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
    public static ZkUniqNoUtil getInstance(String address) {
        if (instance == null) {
            synchronized (ZkUniqNoUtil.class) {
                if (instance == null) {
                    instance = new ZkUniqNoUtil();
                    instance.init(address);
                }
            }
        }
        return instance;
    }

    public  String getUuid(String prefix) {
        String path = "/uniqNo";
        if (StringUtils.isEmpty(prefix)) {
            prefix = path+"/ZK_";
        } else {
            prefix = path+"/"+prefix;
        }
        boolean exists = zkClient.exists(path);
        if (!exists) {
            zkClient.createPersistent(path);
        }
        String ephemeralSequential = zkClient.createEphemeralSequential(prefix, null);
        return ephemeralSequential;
    }


}
