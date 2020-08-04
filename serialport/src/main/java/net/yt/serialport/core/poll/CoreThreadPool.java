package net.yt.serialport.core.poll;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Auth : xiao.yunfei
 * Date : 2019/10/11 20:24
 * Package name : net.yt.serialport.core.poll
 * Des :
 */
class CoreThreadPool extends ThreadPoolExecutor {

    /**
     * 记录运行中任务
     */
    private LinkedBlockingQueue<Runnable> workBlockingQueue = new LinkedBlockingQueue<>();

    CoreThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        workBlockingQueue.add(r);//保存在运行的任务
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        workBlockingQueue.remove(r);//移除关闭的任务
    }

    /**
     * Description: 正在运行的任务
     *
     * @return LinkedBlockingQueue<Runnable><br>
     */
    LinkedBlockingQueue<Runnable> getWorkBlockingQueue() {
        return workBlockingQueue;
    }
}
