package ClassLoadAndForName;

/**
 * @author dataochen
 * @Description
 * classLoad 和Class.forName的区别比较
 * 结果：
 * 俩者都会动态加载类文件到JVM，Class.forName会执行静态块和静态变量（类进行了初始化），而classLoad只是把类加载到虚拟机中
 *
 * @date: 2019/6/5 18:30
 */
public class Main {

    public static void main(String[] args) throws Exception {
        forName();
        System.out.println("====");
        classLoad();
    }

    private static void classLoad() throws ClassNotFoundException {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("ClassLoadAndForName.Test");
    }

    private static void forName() throws ClassNotFoundException {
        Class<?> aClass = Class.forName("ClassLoadAndForName.Test");
    }
}
