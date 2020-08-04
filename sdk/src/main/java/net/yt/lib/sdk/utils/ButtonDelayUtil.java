package net.yt.lib.sdk.utils;

/**
 * @content:按键延时工具类,用于防止按键连点
 * @time:2019-5-19
 * @build:zhouqiang
 */

public class ButtonDelayUtil {

    private static final long MIN_CLICK_DELAY_TIME = 300L;
    private static long lastClickTime;

    public static boolean isFastClick() {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            return true;
        } else {
            return false;
        }
    }
}
