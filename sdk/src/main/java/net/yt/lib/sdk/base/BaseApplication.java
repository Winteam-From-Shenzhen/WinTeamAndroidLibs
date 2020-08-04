package net.yt.lib.sdk.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import net.yt.lib.log.L;
import net.yt.lib.sdk.core.ActivityLifecycleCallbacksImpl;
import net.yt.lib.sdk.core.AppHelper;
import net.yt.lib.sdk.core.ApplicationDelegate;

import java.util.List;

public class BaseApplication extends Application{
    private ApplicationDelegate mApplicationDelegate;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mApplicationDelegate = new ApplicationDelegate(base);
        mApplicationDelegate.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(!isDefaultAPP(this)){
            return;
        }

        AppHelper.I().init(this);
        mApplicationDelegate.onCreate(this);
        //Activity生命周期回调
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mApplicationDelegate.onTerminate(this);
    }

    // 一个应用可能会起几个进程，每一个进程会调用一次Application的onCreate，
    // 为了只在主进程初始化一次，使用pid来判断
    private boolean isDefaultAPP(Context context) {
        L.w("APP launcher pid = " + android.os.Process.myPid());
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                L.d("APP launcher " + this.toString() + " process = " + proInfo.processName + " package name = " + getPackageName());
                if (proInfo.processName != null && getPackageName().equals(proInfo.processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
