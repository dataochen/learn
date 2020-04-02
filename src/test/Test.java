import java.lang.reflect.InvocationTargetException;

/**
 * @author dataochen
 * @Description
 * @date: 2019/6/4 14:48
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        String property = System.getProperty("java.net.preferIPv4Stack");
        System.out.println(property);
        System.out.println();
    }
}
