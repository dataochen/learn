package Object;

import java.security.PrivilegedActionException;

/**
 * @author dataochen
 * @Description
 * @date: 2020/6/28 20:31
 */
public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, PrivilegedActionException {
        long[] l = {1L, 2L};
        long[] clone = l;
//        long[] clone = l.clone();
        clone[1] = 3L;
        System.out.println(l[1]);
        System.out.println(clone[1]);
    }
}
