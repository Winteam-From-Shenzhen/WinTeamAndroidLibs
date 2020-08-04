package net.yt.lib.memorymonitor;

import android.app.Application;
import android.content.Context;

import net.yt.lib.memorymonitor.util.MemoryUtil;
import net.yt.lib.memorymonitor.util.PixAndDpUtil;
import net.yt.lib.memorymonitor.util.ProcessUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MemoryMonitor {
    private static class InstanceHolder {
        private static MemoryMonitor sInstance = new MemoryMonitor();
    }

    private MemoryMonitor() {
    }

    public static MemoryMonitor getInstance() {
        return InstanceHolder.sInstance;
    }

    private Context mContext;
    private Timer mTimer;
    private FloatCurveView mFloatCurveView;
    private boolean mIsRunning;

    private static final long DURATION = 500;


    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("u must init with application context");
        }
        this.mContext = context;
    }

    public void start(final @FloatCurveView.MemoryType String type) {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        if (mFloatCurveView == null) {
            mFloatCurveView = new FloatCurveView(mContext);
        }
        FloatCurveView.Config config = new FloatCurveView.Config();
        config.height = PixAndDpUtil.dp2px(250, mContext); //mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_height);
        config.padding = PixAndDpUtil.dp2px(8, mContext); //mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_padding);
        config.dataSize = 40;
        config.yPartCount = 8;
        config.type = type;
        mFloatCurveView.attachToWindow(config);

        if (mTimer == null) {
            mTimer = new Timer();
        }
        TimerTask timerTask = null;
        switch (type) {
            case FloatCurveView.MEMORY_TYPE_PSS:
                timerTask = new PssTimerTask(mContext, mFloatCurveView);
                break;
            case FloatCurveView.MEMORY_TYPE_HEAP:
                timerTask = new HeapTimerTask(mFloatCurveView);
                break;
            default:
                break;
        }
        mTimer.scheduleAtFixedRate(timerTask, 0, DURATION);
        mIsRunning = true;
    }

    public static class PssTimerTask extends MemoryTimerTask {
        private Context mContext;

        public PssTimerTask(Context context, FloatCurveView floatCurveView) {
            super(floatCurveView);
            this.mContext = context;
        }

        @Override
        public float getValue() {
            final int pid = ProcessUtil.getCurrentPid();
            MemoryUtil.PssInfo pssInfo = MemoryUtil.getAppPssInfo(mContext, pid);
            return (float) pssInfo.totalPss / 1024;
        }
    }

    public static class HeapTimerTask extends MemoryTimerTask {

        public HeapTimerTask(FloatCurveView floatCurveView) {
            super(floatCurveView);
        }

        @Override
        public float getValue() {
            final MemoryUtil.DalvikHeapMem dalvikHeapMem = MemoryUtil.getAppDalvikHeapMem();
            return (float) dalvikHeapMem.allocated / 1024;
        }
    }

    public static abstract class MemoryTimerTask extends TimerTask {
        protected FloatCurveView mFloatCurveView;

        public MemoryTimerTask(FloatCurveView floatCurveView) {
            this.mFloatCurveView = floatCurveView;
        }

        public abstract float getValue();

        @Override
        public void run() {
            mFloatCurveView.addData(getValue());
            mFloatCurveView.post(new Runnable() {
                @Override
                public void run() {
                    mFloatCurveView.setText(getValue());
                }
            });
        }
    }


    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mFloatCurveView != null) {
            mFloatCurveView.release();
            mFloatCurveView = null;
        }
        mIsRunning = false;
    }

    public void toggle(final @FloatCurveView.MemoryType String type) {
        if (mIsRunning) {
            stop();
        } else {
            start(type);
        }
    }
}
