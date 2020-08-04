package net.yt.util;

import android.text.TextUtils;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 10:10
 * Package name : net.yt.util
 * Des :
 */
public class SerialLog {

    private static final String TAG = "SerialLog";

    private static LogPrinter printer;

    /**
     * 必须调用，二者选其一
     */
    public static void init() {
        init(TAG);
    }

    /**
     * 必须调用，二者选其一
     */
    public static void init(String tag) {

        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (printer == null) {
            printer = new LogPrinter(tag);
        }
    }


    public static void d(String msg) {

        if (printer != null) {
            printer.d(msg);
        }
    }

    public static void w(String msg) {
        if (printer != null) {
            printer.w(msg);
        }
    }

    public static void i(String msg) {
        if (printer != null) {
            printer.i(msg);
        }
    }

    public static void e(String msg) {
        if (printer != null) {
            printer.e(msg);
        }
    }

}
