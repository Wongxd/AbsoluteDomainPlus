package com.wongxd.absolutedomain.util.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wongxd on 2017/9/1.
 * <p>
 * 线程管理工具
 * <p>
 * http://gudong.name/2017/05/03/thread-pool-intro.html
 * <p>
 * <p>
 * <p>
 * <p>
 * corePoolSize
 * 线程池的核心线程数。在没有设置 allowCoreThreadTimeOut 为 true 的情况下，核心线程会在线程池中一直存活，即使处于闲置状态。
 * maximumPoolSize
 * 线程池所能容纳的最大线程数。当活动线程(核心线程+非核心线程)达到这个数值后，后续任务将会根据 RejectedExecutionHandler 来进行拒绝策略处理。
 * keepAliveTime
 * 非核心线程闲置时的超时时长。超过该时长，非核心线程就会被回收。若线程池通过 allowCoreThreadTimeOut() 方法设置 allowCoreThreadTimeOut 属性为 true，则该时长同样会作用于核心线程，AsyncTask 配置的线程池就是这样设置的。
 * unit
 * keepAliveTime 时长对应的单位。
 * workQueue
 * 线程池中的任务队列，通过线程池的 execute() 方法提交的 Runnable 对象会存储在该队列中。
 * ThreadFactory
 * 线程工厂，功能很简单，就是为线程池提供创建新线程的功能。这是一个接口，可以通过自定义，做一些自定义线程名的操作。
 * RejectedExecutionHandler
 * 当任务无法被执行时(超过线程最大容量 maximum 并且 workQueue 已经被排满了)的处理策略，这里有四种任务拒绝类型。
 */

public class ThreadManager {
    //参数初始化
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量大小
    private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //线程池最大容纳线程数
    private static final int maximumPoolSize = CPU_COUNT * 2 + 1;
    //线程空闲后的存活时长
    private static final long keepAliveTime = 10000L;


    private ThreadManager() {

    }

    private static ThreadManager instance = new ThreadManager();
    private ThreadPoolProxy longPool;
    private ThreadPoolProxy shortPool;

    public static ThreadManager getInstance() {
        return instance;
    }


    /**
     * 联网比较耗时
     * cpu的核数*2+1
     *
     * @return
     */
    public synchronized ThreadPoolProxy createLongPool() {
        if (longPool == null) {
            longPool = new ThreadPoolProxy(corePoolSize, maximumPoolSize, keepAliveTime);
        }
        return longPool;
    }

    /**
     * 操作本地文件
     *
     * @return
     */
    public synchronized ThreadPoolProxy createShortPool() {
        if (shortPool == null) {
            shortPool = new ThreadPoolProxy(corePoolSize, maximumPoolSize, keepAliveTime);
        }
        return shortPool;
    }

    private static class ThreadPoolProxy {

        private ThreadPoolExecutor pool;
        private int corePoolSize;
        private int maximumPoolSize;
        private long time;


        /**
         * @param corePoolSize    核心线程数量大小
         * @param maximumPoolSize 线程池最大容纳线程数
         * @param time            线程空闲后的存活时长
         */
        ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.time = time;

        }


        //任务过多后，存储任务的一个阻塞队列
        BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();

        //线程的创建工厂  功能很简单，就是为线程池提供创建新线程的功能。这是一个接口，可以通过自定义，做一些自定义线程名的操作。
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "Wongxd_AsyncTask #" + mCount.getAndIncrement());
            }
        };

        //线程池任务满载后采取的任务拒绝策略
        RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();


        /**
         * 执行任务
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {
            if (pool == null) {
                //线程池对象，创建线程
                /*
                 * 1. 线程池里面管理多少个线程
                 * 2. 如果排队满了, 额外的开的线程数
                 * 3. 如果线程池没有要执行的任务 存活多久
                 * 4. 时间的单位
                 * 5. 如果 线程池里管理的线程都已经用了,剩下的任务 临时存到  BlockingQueue<Runnable> 对象中 排队
                 * 6. 线程的创建工厂  功能很简单，就是为线程池提供创建新线程的功能。这是一个接口，可以通过自定义，做一些自定义线程名的操作。
                 * 7. 线程池任务满载后采取的任务拒绝策略
				 */
                pool = new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        time,
                        TimeUnit.MILLISECONDS,
                        workQueue,
                        threadFactory,
                        rejectHandler
                );
            }
            pool.execute(runnable); // 调用线程池 执行异步任务
        }

        /**
         * 取消任务
         *
         * @param runnable
         */
        public void cancel(Runnable runnable) {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.remove(runnable); // 取消异步任务
            }
        }
    }
}
