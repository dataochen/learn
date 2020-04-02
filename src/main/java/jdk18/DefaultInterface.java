package jdk18;

import java.util.function.Function;

/**
 * @author dataochen
 * @Description
 * @date: 2020/3/31 16:55
 */
@FunctionalInterface
public interface DefaultInterface <T,R>{


    void test();

    default Function<T, R>  test2() {
        System.out.println(2);
        return null;
    }
}
