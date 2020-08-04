package net.yt.lib.memorymonitor.util;

import java.util.concurrent.TimeUnit;

public class MethodTracer {
    public static final String TAG = "MethodTracer";

    private static long sNow;

    public static void start() {
        sNow = System.nanoTime();
    }

    public static long endToResultForNano() {
        return System.nanoTime() - sNow;
    }

    public static long endToResultForMillis() {
        return TimeUnit.NANOSECONDS.toMillis(endToResultForNano());
    }

    public static void endToOutput(String name) {
        L.d(TAG, name + " spent " + endToResultForMillis() + " millis");
    }
}
