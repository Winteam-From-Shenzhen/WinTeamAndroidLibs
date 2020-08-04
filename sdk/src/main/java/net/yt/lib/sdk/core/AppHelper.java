package net.yt.lib.sdk.core;

import android.app.Application;

public class AppHelper {
    private static AppHelper instance = null;
    private static Application mApp;

    public static AppHelper I() {
        if (null == instance) {
            synchronized (AppHelper.class) {
                if (null == instance) {
                    instance = new AppHelper();
                }
            }
        }
        return instance;
    }

    public static void init(Application application) {
        mApp = application;
    }

    public Application getApp() {
        return mApp;
    }
}
