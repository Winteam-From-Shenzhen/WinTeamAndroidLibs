package net.yt.lib.memorymonitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringDef;
import net.yt.lib.memorymonitor.curvechart.CurveChartView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description: <悬浮内存显示曲线图，M为内存单位>
 */

public class FloatCurveView extends RelativeLayout {

    private static final String VALUE_FORMAT = "%.1fM";
    private static final String VALUE_FORMAT_TXT = "%1$s:%2$.1fM";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MEMORY_TYPE_PSS, MEMORY_TYPE_HEAP})
    public @interface MemoryType {
    }

    public static final String MEMORY_TYPE_PSS = "pss";
    public static final String MEMORY_TYPE_HEAP = "heap";

    public static class Config {
        public int height = WindowManager.LayoutParams.MATCH_PARENT;
        public int width = WindowManager.LayoutParams.MATCH_PARENT;
        public int padding = 0;
        public int x = 0;
        public int y = 0;
        public int dataSize = 10;
        public int yPartCount = 5;
        public
        @MemoryType
        String type;
    }

    private FloatContainer mFloatContainer;

    private CurveChartView mCurveChartView;

    private TextView mNameAndValueTv;

    public FloatCurveView(Context context) {
        this(context, null);
    }

    public FloatCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    private void setUp() {
        mFloatContainer = new FloatContainer(getContext());
        inflate(getContext(), R.layout.memorymonitor_view_floatcurveview, this);
        mCurveChartView = this.findViewById(R.id.mem_monitor_view_floatcurveview);
        mNameAndValueTv = this.findViewById(R.id.mem_monitor_view_namevalue);
    }

    private String mPrefix;

    public void attachToWindow(Config config) {
        mPrefix = config.type;
        net.yt.lib.memorymonitor.curvechart.Config.Builder builder = new net.yt.lib.memorymonitor.curvechart.Config.Builder();
        builder.setYFormat(VALUE_FORMAT)
                .setDataSize(config.dataSize)
                .setMaxValueMulti(1.2f)
                .setMinValueMulti(0.8f)
                .setXTextPadding(70)
                .setYPartCount(config.yPartCount)
                .setYLabelSize(20f);
        mCurveChartView.setUp(builder.create());
        mCurveChartView.setPadding(config.padding, config.padding, config.padding, config.padding);
        mFloatContainer.attachToWindow(this, Gravity.START | Gravity.TOP, config.x, config.y, config.width, config.height);
    }

    public void release() {
        mFloatContainer.release();
    }

    @SuppressLint("DefaultLocale")
    public void setText(float value) {
        mNameAndValueTv.setText(String.format(VALUE_FORMAT_TXT, mPrefix, value));
    }

    public void addData(float data) {
        mCurveChartView.addData(data);
    }
}
