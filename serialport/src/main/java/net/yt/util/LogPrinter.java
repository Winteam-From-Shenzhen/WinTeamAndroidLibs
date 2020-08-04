package net.yt.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 10:12
 * Package name : net.yt.util
 * Des :
 */
class LogPrinter {

    /**
     * 最近相关的 trace index, 前两个为线程相关.
     */
    private static final int MIN_STACK_OFFSET = 2;


    private static final char TOP_CORNER = '╔';
    private static final char BOTTOM_CORNER = '╚';
    private static final char CENTER_LINE = '║';
    private static final String DIVIDER = "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════";

    private String tag;

    LogPrinter(String tag) {
        this.tag = tag;
    }

    void d(String msg) {

        Log.d(tag, getMsg(msg));
    }

    void w(String msg) {
        Log.w(tag, getMsg(msg));
    }

    void i(String msg) {
        Log.i(tag, getMsg(msg));
    }

    void e(String msg) {
        Log.e(tag, getMsg(msg));
    }


    /**
     * 获取需要输出的文字
     *
     * @param msg msg
     * @return format msg
     */
    private String getMsg(String msg) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("      ").append("\n")
                .append(TOP_CORNER)
                .append(DIVIDER)
                .append("\n");
        getDetailInfo(buffer);
        buffer.append(CENTER_LINE).append("   ").append(msg).append("\n");
        buffer.append(BOTTOM_CORNER).append(DIVIDER);
        return buffer.toString();
    }

    /**
     * 获取详细信息
     *
     * @param buffer buffer
     */
    private void getDetailInfo(StringBuffer buffer) {
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        int offset = getStackOffset(element);
        if (offset == 0) {
            return;
        }
        StackTraceElement trace = element[offset];
        buffer.append(CENTER_LINE).append(trace.getClassName()).append("\n");
        buffer.append(CENTER_LINE)
                .append("   ")
                .append(trace.getMethodName())
                .append("( ")
                .append(trace.getLineNumber())
                .append(" )")
                .append("\n");
    }


    /**
     * 获取详细信息的 StackTraceElement 的 index
     *
     * @param trace trace
     * @return index
     */
    private int getStackOffset(StackTraceElement[] trace) {
        if (trace == null || trace.length == 0) {
            return 0;
        }
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            String printName = LogPrinter.class.getName();
            String logUtilName = SerialLog.class.getName();

            if (!TextUtils.equals(name, printName) && !TextUtils.equals(name, logUtilName)) {
                return i;
            }
        }
        return -1;
    }

}
