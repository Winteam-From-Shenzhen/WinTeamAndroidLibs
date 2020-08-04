package net.yt.lib.memorymonitor.util;

import android.util.Log;

public class L {
    private static final String TAG = "Memory";

    private static boolean sIsDebug;

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (sIsDebug) {
            Log.d(tag, msg);
        }
    }

    public static boolean isDebug() {
        return sIsDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        L.sIsDebug = isDebug;
    }
}
