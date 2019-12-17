package Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author dataochen
 * @Description
 * 1.getDeclaredFields getFields区别
 * 2.setAccessible 方法作用 true：跳过安全检查，暴露数据
 * 3.
 * @date: 2019/6/5 21:31
 */
public class Main  {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        ClassLoader classLoader = Test.class.getClassLoader();
        Class<?> test = classLoader.loadClass("Reflect.Test");
        Object o = test.newInstance();

//        publish的属性
//        Field[] fields = test.getFields();
//        for (Field field : fields) {
//        System.out.println(field);
//        }
        System.out.println("====1===");
//        所有属性
        Field[] declaredFields = test.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField.getDeclaringClass());
            declaredField.setAccessible(false);
            Object o1 = declaredField.get(o);
            System.out.println(declaredField);
        }
//
        System.out.println("===2====");
        Method test2 = test.getDeclaredMethod("test", String.class);
        test2.setAccessible(false);
        Object test21 = test2.invoke(o, "test2");
        System.out.println(test21);
//
        System.out.println("===3==");
        Method test1 = test.getMethod("test", String.class);
        Object cdt = test1.invoke(o, "cdt");
        System.out.println(cdt);
    }
}
