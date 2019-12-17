package SecurityManager;

/**
 * @author dataochen
 * @Description
 * 默认的安全管理器配置文件是 $JAVA_HOME/jre/lib/security/java.policy
 * 安全管理器通过执行运行阶段检查和访问授权，以实施应用所需的安全策略，从而保护资源免受恶意操作的攻击。
 * @date: 2019/6/6 14:42
 */
public class Main {
    public static void main(String[] args) {
        String s = System.getProperty("java.security.manager");
        System.out.println(s);
        SecurityManager securityManager = new SecurityManager();
        securityManager.checkRead("cdt.txt");
    }
}
