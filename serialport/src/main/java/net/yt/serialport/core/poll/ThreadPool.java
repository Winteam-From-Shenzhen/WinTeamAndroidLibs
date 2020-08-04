package net.yt.serialport.core.poll;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 11:59
 * Package name : net.yt.serialport.core.poll
 * Des : 线程池
 */
public class ThreadPool {

    private static ThreadPool threadPool;
    private static int corePoolSize = 3;
    private static int maxPoolSize = 4;
    private static int keepAliveTime = 1000;
    private CoreThreadPool poolExecutor;

    public static ThreadPool getThreadPool() {
        if (threadPool == null) {
            synchronized (ThreadPool.class) {
                if(threadPool == null) {
                    threadPool = new ThreadPool();
                }
            }
        }
        return threadPool;
    }


    private ThreadPool() {
        if (poolExecutor == null) {
            poolExecutor = new CoreThreadPool(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(4));
        }

    }

    public void addTask(Runnable runnable) {
        BlockingQueue<Runnable> waitThreadQueue = poolExecutor.getQueue();//Returns the task queue
        LinkedBlockingQueue<Runnable> workThreadQueue = poolExecutor.getWorkBlockingQueue();//Returns the running work


        if (!waitThreadQueue.contains(runnable) && !workThreadQueue.contains(runnable)) {
            //判断任务是否存在正在运行的线程或存在阻塞队列，
            // 不存在的就加入线程池（这里的比较要重写equals()）
            poolExecutor.execute(runnable);//添加到线程池
        }
    }

}
