package net.yt.lib.log;

public class L {
    public static void v(String msg) {
        Log.getInstance().v(msg);
    }

    public static void vv(String key, String msg) {
        Log.getInstance().vv(key, msg);
    }

    public static void d(String msg) {
        Log.getInstance().d(msg);
    }

    public static void dd(String key, String msg) {
        Log.getInstance().dd(key, msg);
    }

    public static void i(String msg) {
        Log.getInstance().i(msg);
    }

    public static void ii(String key, String msg) {
        Log.getInstance().ii(key, msg);
    }

    public static void w(String msg) {
        Log.getInstance().w(msg);
    }

    public static void ww(String key, String msg) {
        Log.getInstance().ww(key, msg);
    }

    public static void e(String msg) {
        Log.getInstance().e(msg);
    }

    public static void ee(String key, String msg) {
        Log.getInstance().ee(key, msg);
    }
}
