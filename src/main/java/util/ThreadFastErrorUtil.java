package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dataochen
 * @Description 快速异步执行工具类
 * 如果有一个线程校验失败或异常 直接返回false 其他线程也将中断结束
 * @date: 2020/11/5 15:48
 */
public class ThreadFastErrorUtil {

    private volatile static ThreadFastErrorUtil instance;

    private ThreadFastErrorUtil() {
    }

    /**
     * 使用单例 防止使用此工具的人每次都不传线程池，导致每次都创建线程池。也为了方便管理。
     * @return
     */
    public static ThreadFastErrorUtil getInstance() {
        return getInstance(null);
    }

    public static ThreadFastErrorUtil getInstance(Executor executor) {
        if (null == instance) {
            synchronized (ThreadFastErrorUtil.class) {
                if (null == instance) {
                    instance = new ThreadFastErrorUtil(executor);
                }
            }
        }
        return instance;
    }

    /**
     * 如果自定义了线程池 优先用自定义的 否则使用默认的
     * @param executor
     */
    private ThreadFastErrorUtil(Executor executor) {
        if (executor == null) {
            executor = new ThreadPoolExecutor(5, 5, 2000,
                    TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024),
                    new FastErrorUtilThreadFactory(), (r, e) -> {
                throw new RejectedExecutionException("Task " + r.toString() +
                        " rejected from " +
                        e.toString());
            }
            );
        }
        this.executor = executor;
    }

    /**
     * 定义此工具类的线程池的线程名称 便于日志定位和管理
     */
    class FastErrorUtilThreadFactory implements ThreadFactory {
        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        FastErrorUtilThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "ThreadFastErrorUtilPool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private Executor executor;

    /**
     * 快速异步校验
     * 如果有一个线程校验失败或异常 直接返回false 其他线程也将中断结束
     *
     * @param tasks
     * @return
     */
    public FastErrorResponse fastCheckAsync(Collection<? extends Callable<FastErrorResponse>> tasks) {
        return invokeAsync(tasks);
    }

    /**
     * 泛型化参数 支持用户自选扩展使用
     * @param tasks
     * @param <T>
     * @return
     */
    public <T extends FastErrorResponse> FastErrorResponse invokeAsync(Collection<? extends Callable<T>> tasks) {
        if (tasks == null) {
            throw new NullPointerException();
        }
        int ntasks = tasks.size();
        if (ntasks == 0) {
            throw new IllegalArgumentException();
        }
        ArrayList<Future<T>> futures = new ArrayList<Future<T>>(ntasks);
        ExecutorCompletionService<T> ecs =
                new ExecutorCompletionService<T>(executor);
        try {
            ExecutionException ee = null;
            Iterator<? extends Callable<T>> it = tasks.iterator();
            futures.add(ecs.submit(it.next()));
            --ntasks;
            int active = 1;

            for (; ; ) {
                Future<T> f = ecs.poll();
                if (f == null) {
                    if (ntasks > 0) {
                        --ntasks;
                        futures.add(ecs.submit(it.next()));
                        ++active;
                    } else if (active == 0) {
                        break;
                    } else {
                        try {
                            f = ecs.take();
                        } catch (Exception e) {
                            //                            直接返回 并中断其他线程
                            FastErrorResponse fastErrorResponse = new FastErrorResponse(false, e.getMessage());
                            return fastErrorResponse;
                        }
                    }
                }
                if (f != null) {
                    --active;
                    try {
                        T t = f.get();
                        if (!t.isStatus()) {
//                            直接返回 并中断其他线程
                            return t;
                        }
                    } catch (Exception e) {
                        //                            直接返回 并中断其他线程
                        FastErrorResponse fastErrorResponse = new FastErrorResponse(false, e.getMessage());
                        return fastErrorResponse;
                    }
                }
            }


        } finally {
//            中断其他线程
            for (int i = 0, size = futures.size(); i < size; i++) {
                futures.get(i).cancel(true);
            }
        }
//        都成功
        return new FastErrorResponse(true, null);
    }

    /**
     * 快速异步校验
     * 如果有一个线程校验失败直接返回false 其他线程也将中断结束
     * 加 超时时间 超时直接失败
     * todo
     *
     * @return
     */


}
