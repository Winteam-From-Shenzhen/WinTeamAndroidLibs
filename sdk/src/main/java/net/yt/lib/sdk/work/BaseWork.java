package net.yt.lib.sdk.work;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

/*
  后台业务work要运行，必须实现无参数静态函数create或者默认无参数构造函数。
  如果create返回null，表示不参与运行。
 */

public abstract class BaseWork implements IWork {
    private HandlerThread mBaseHandlerThread;
    private Handler mBaseWorkHandler;

    @Override
    public void onInit(Context c) {
        synchronized (BaseWork.this) {
            mBaseHandlerThread = new HandlerThread(this.getName());
            mBaseHandlerThread.start();
            mBaseWorkHandler = new Handler(mBaseHandlerThread.getLooper());
        }
    }

    @Override
    public void onUninit(Context c) {
        synchronized (BaseWork.this) {
            mBaseWorkHandler.removeCallbacksAndMessages(null);
            mBaseWorkHandler = null;
            mBaseHandlerThread.quitSafely();
            mBaseHandlerThread = null;
        }
    }

    @Override
    public String getName() {
        String className = this.getClass().getName();
        return className.substring(className.lastIndexOf(".")+1);
    }

    public static final void register(IWork work){
        WorkService.register(work);
    }

    public static final void unregister(IWork work){
        WorkService.unregister(work);
    }

    protected void post(Runnable r, long delayMillis){
        synchronized (BaseWork.this) {
            if (null != mBaseWorkHandler) {
                mBaseWorkHandler.postDelayed(r, delayMillis);
            }
        }
    }

    protected void post(Runnable r){
        post(r, 0L);
    }

    //如果参数为空，表示删除所有信息
    protected void removeRunnable(Runnable r){
        synchronized (BaseWork.this) {
            if (null != mBaseWorkHandler) {
                if(r != null) {
                    mBaseWorkHandler.removeCallbacks(r);
                }else{
                    mBaseWorkHandler.removeCallbacksAndMessages(null);
                }
            }
        }
    }

}
