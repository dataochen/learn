package ClassLoad;

/**
 * @author dataochen
 * @Description
 * @date: 2019/6/5 20:22
 */
public class Main {
    public static void main(String[] args) {
        ClassLoader classLoader = Main.class.getClassLoader();
        System.out.println(classLoader);
        ClassLoader parent = classLoader.getParent();
        System.out.println(parent);
        ClassLoader parent1 = parent.getParent();
        System.out.println(parent1);

        String var1 = System.getProperty("java.class.path");
        System.out.println(var1);
        String var0 = System.getProperty("java.ext.dirs");
        System.out.println(var0);
    }
}
