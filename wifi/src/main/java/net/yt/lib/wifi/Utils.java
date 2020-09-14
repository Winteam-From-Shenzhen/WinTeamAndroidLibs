package net.yt.lib.wifi;

import android.os.Build;
import android.os.Looper;

public class Utils {

    //判断是否当前线程
    public static boolean isOnMainThread() {
        //return Looper.myLooper() == Looper.getMainLooper();
        //return Thread.currentThread() == Looper.getMainLooper().getThread();
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    public static boolean isOSVersionNougat() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return true;
        }
        return false;
    }

    public static boolean isStringEquals(String s1, String s2){
        if(s1 == null && s2 == null){
            return true;
        }

        if(null == s1 && null != s2 || null != s1 && null == s2){
            return false;
        }

        if(s1.equals(s2)){
            return true;
        }
        return false;
    }

}
