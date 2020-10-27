package jdk18;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author dataochen
 * @Description
 * @date: 2020/10/26 16:13
 */
public class AllReferenceJava {

    public static void main(String[] args) throws InterruptedException {
////        ReferenceQueue referenceQueue = new ReferenceQueue();
//        Bean bean = new Bean();
//        bean.setA("a");
//////        Bean bean2 = bean;
//        SoftReference<Bean> beanSoftReference = new SoftReference<>(bean);
//        bean = null;
//        Bean bean1 = beanSoftReference.get();
//        System.out.println(beanSoftReference.get());
////        WeakReference<Bean> beanWeakReference = new WeakReference<>(bean,referenceQueue);
//////        Bean bean1 = beanWeakReference.get();
//        PhantomReference<Bean> beanPhantomReference = new PhantomReference<>(bean,referenceQueue);
////        bean = null;
//////        beanWeakReference = null;
//////        beanSoftReference = null;
////        System.gc();
////        Thread.sleep(500);
////        Reference poll ;
////        while ((poll = referenceQueue.poll()) != null) {
////            System.out.println(poll);
////        }
//////        System.out.println(referenceQueue.poll());
////        testThreadLocal();
        t();
    }

    static void testThreadLocal() {
        ThreadLocal<String> stringThreadLocal = new ThreadLocal<>();
        ThreadLocal<String> stringThreadLocal2 = new ThreadLocal<>();
        stringThreadLocal.set("1");
        stringThreadLocal2.set("2");
        System.out.println(stringThreadLocal.get());
        stringThreadLocal = null;
        stringThreadLocal.remove();
    }

    static void t() throws InterruptedException {
//        HashMap<Object, Object> map = new HashMap<>();
        Object obj = new Object();
        ReferenceQueue referenceQueue = new ReferenceQueue();

//        map.put(obj, 1);
        WeakReference weakReference = new WeakReference(obj, referenceQueue);
        SoftReference<Object> objectSoftReference = new SoftReference<>(obj);
        PhantomReference phantomReference = new PhantomReference(obj, referenceQueue);
//        map.put(weakReference.get(), 1);
//        map.keySet().forEach(System.out::println);
//        map.remove(obj);
        obj = null;
////        注意o1是强引用
//        Object o1 = weakReference.get();
        System.gc();
        Thread.sleep(500);
        System.out.println(referenceQueue.poll());
//        map.keySet().forEach(System.out::println);
//
//
//
//        WeakHashMap<Object, Object> objectObjectWeakHashMap = new WeakHashMap<>();
//        System.out.println("===");
//        Object o = new Object();
//        objectObjectWeakHashMap.put(o, 1);
//        o = null;
//        System.gc();
//        Thread.sleep(500);
//        objectObjectWeakHashMap.keySet().forEach(System.out::println);
    }

    static void hitObj() {
        Object obj = new Object();
        ReferenceQueue referenceQueue = new ReferenceQueue();
        PhantomReference phantomReference = new PhantomReference(obj, referenceQueue);

    }
}
