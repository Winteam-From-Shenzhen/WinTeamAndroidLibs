package net.yt.lib.sdk.core;

import android.app.Activity;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static ActivityManager sInstance = new ActivityManager();
    private List<Activity> activities = new ArrayList<>();
    private WeakReference<Activity> sCurrentActivityWeakRef;

    public synchronized static ActivityManager I() {
        return sInstance;
    }

    public void addActivity(Activity activity){
        activities.add(activity);
    }

    public void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }

    public boolean isTopActivity(Class cls) {
        Activity activity = getCurrentActivity();
        if (activity == null){
            return false;
        }
        String runningClassName = activity.getClass().getName();
        String clsName = cls.getName();
        return TextUtils.equals(runningClassName, clsName);

    }

    public boolean isTopActivity(String className) {
        Activity activity = getCurrentActivity();
        if (activity == null){
            return false;
        }
        String runningClassName = activity.getClass().getName();
        String clsName = className;
        return TextUtils.equals(runningClassName, clsName);
    }
}
