package Object;

/**
 * @author dataochen
 * @Description
 * @date: 2020/6/28 20:31
 */
public class Test {
    public static void main(String[] args) {
        Thread thread = new Thread();
        thread.start();
        thread.interrupt();
    }
}
