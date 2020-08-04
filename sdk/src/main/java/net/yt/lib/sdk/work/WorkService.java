package net.yt.lib.sdk.work;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import net.yt.lib.log.L;
import net.yt.lib.sdk.R;
import net.yt.lib.sdk.core.AppHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WorkService extends Service {
    private static final String TAG = "WorkService";
    private static final String CHANNEL_ID = "WorkService";

    private final long LOG_DURING = 5 * 60 * 1000L;

    private static WorkService sInstance = null;
    private static ArrayList<IWork> mPreInstallWorks = new ArrayList<>();
    private static ArrayList<IWork> mPreUninstallWorks = new ArrayList<>();
    private ArrayList<IWork> mWorks;
    private Handler mHandler;

    private Runnable mRegisterWorkRunning = new Runnable() {
        @Override
        public void run() {
            synchronized (WorkService.class) {
                for(IWork work : mPreInstallWorks){
                    if(!mWorks.contains(work)) {
                        mWorks.add(work);
                        work.onInit(WorkService.this);
                    }
                }
                mPreInstallWorks.clear();
            }
        }
    };
    private Runnable mUnregisterWorkRunning = new Runnable() {
        @Override
        public void run() {
            synchronized (WorkService.class) {
                for(IWork work : mPreUninstallWorks){
                    if(mWorks.contains(work)) {
                        mWorks.remove(work);
                        work.onUninit(WorkService.this);
                    }
                }
                mPreUninstallWorks.clear();
            }
        }
    };
    private Runnable mLogRunning = new Runnable() {
        @Override
        public void run() {
            L.dd(TAG, log());
            mHandler.postDelayed(mLogRunning, LOG_DURING);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
       扫描特定包下的所有带有WORKANNOTATION的注解类，实例化后注册，如果work需要特别的实例化，需要实现静态函数creat。
       必须在子线程调用，否则会阻塞主线程
     */
    public static void launch(Context c, String[] packageNameList) {
        if(null == packageNameList || packageNameList.length == 0){
            throw new IllegalArgumentException("WorkService launch packageNameList is empty !");
        }

        L.dd(TAG, "launch begin = " + System.currentTimeMillis());
        for(int i =0; i<packageNameList.length; i++) {
            List<String> allClassStr = ClassUtil.getClassName(c, packageNameList[i]);
            for (int j = 0; j < allClassStr.size(); j++) {
                String classStr = allClassStr.get(j);
                //L.dd(TAG, "launch begin classStr = " + classStr);
                Class<?> className = null;
                try {
                    className = Class.forName(classStr);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (null == className) {
                    L.ww(TAG, "Class  = " + classStr + " no found !");
                    continue;
                }

                WORKANNOTATION classAnn = className.getAnnotation(WORKANNOTATION.class);
                if (classAnn == null) {
                    continue;
                }

                Method createMethod = null;
                try {
                    createMethod = className.getMethod("create");
                }catch (NoSuchMethodException e){
                    e.printStackTrace();
                }
                IWork work = null;
                //如果没有创建的函数，则实例化无参数的构造函数
                if(null == createMethod){
                    L.ee(TAG, "Class  = " + classStr + " has no create method !");
                    try {
                        work = (IWork)className.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        L.ee(TAG, "Class  = " + classStr + " newInstance throw InstantiationException !");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        L.ee(TAG, "Class  = " + classStr + " newInstance throw IllegalAccessException !");
                    };
                }else{
                    try {
                        work = (IWork) createMethod.invoke(null);
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                        L.ee(TAG, "Class  = " + classStr + " create throw IllegalAccessException !");
                    }catch (InvocationTargetException e){
                        e.printStackTrace();
                        L.ee(TAG, "Class  = " + classStr + " create throw InvocationTargetException !");
                    }
                }

                if(null == work){
                    L.ee(TAG, "Class  = " + classStr + " create fail !");
                    continue;
                }

                L.d("Work className = " + className.toString() + " register sucess ! ");
                WorkService.register(work);
            }
        }
        L.dd(TAG, "launch end = " + System.currentTimeMillis());

        Intent intent = new Intent(AppHelper.I().getApp(), WorkService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppHelper.I().getApp().startForegroundService(intent);
        } else {
            AppHelper.I().getApp().startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.dd(TAG, "Work service create ... ");

        initServiceFrontDesk();
        synchronized (WorkService.class) {
            sInstance = this;
            mWorks = new ArrayList<>();
            mWorks.clear();
        }
        mHandler = new Handler();
        postAddWork();
        postRemoveWork();

        mHandler.postDelayed(mLogRunning, LOG_DURING);
    }

    private void initServiceFrontDesk() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) AppHelper.I().getApp().getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel;
            channel = new NotificationChannel(CHANNEL_ID, "APP业务服务", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false);//设置提示灯
            channel.setShowBadge(true);//显示logo
            channel.setDescription("APP业务服务");//设置描述
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("APP业务服务")//标题
                    .setContentText("运行中...")//内容
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_alert))
                    .build();
            startForeground(110, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.dd(TAG, "Work service destroy ... ");
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

        synchronized (WorkService.class){
            sInstance = null;
            mPreInstallWorks.clear();
            mPreUninstallWorks.clear();
            for(IWork work : mWorks){
                work.onUninit(this);
            }
            mWorks.clear();
            mWorks = null;
        }
    }

    public static void register(IWork work){
        synchronized (WorkService.class){
            mPreInstallWorks.add(work);
            if(null != sInstance){
                sInstance.postAddWork();
            }
        }
    }

    public static void unregister(IWork work){
        synchronized (WorkService.class){
            mPreUninstallWorks.add(work);
            if(null != sInstance){
                sInstance.postRemoveWork();
            }
        }
    }

    public void postAddWork(){
        mHandler.post(mRegisterWorkRunning);
    }

    public void postRemoveWork(){
        mHandler.post(mUnregisterWorkRunning);
    }

    public String log(){
        String result = "";
        synchronized (WorkService.class) {
            for (IWork work : mWorks) {
                result += " " + work.getName() + " ";
            }
        }
        return result;
    }

}
