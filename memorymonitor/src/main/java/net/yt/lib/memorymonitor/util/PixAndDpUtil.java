package net.yt.lib.memorymonitor.util;

import android.content.Context;
import android.util.TypedValue;

public class PixAndDpUtil {
    //dp转换成像素
    public static int dp2px(int value, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,context.getResources().getDisplayMetrics() );
    }

    //像素转换成dp
    public static int dp2sp(int value, Context context) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,context.getResources().getDisplayMetrics() );
    }
}
