package net.yt.lib.sdk.core;

import android.app.Application;
import android.content.Context;

public interface IAppLife {
    public static final int MAX_PRIORITY = 10;
    public static final int MIN_PRIORITY = 1;
    public static final int NORM_PRIORITY = 5;

    /**
     * 返回组件的优先级，优先级范围为[1-10]，10为最高，1为最低，默认优先级是5
     *
     * @return
     */
    int getPriority();

    void attachBaseContext(Context base);

    void onCreate(Application application);

    void onTerminate(Application application);

}
