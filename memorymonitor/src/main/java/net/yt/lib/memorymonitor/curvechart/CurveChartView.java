package net.yt.lib.memorymonitor.curvechart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import net.yt.lib.memorymonitor.R;

import java.util.ArrayList;
import java.util.List;

public class CurveChartView extends View {
    private Config mConfig;
    private float yMaxValue;
    private float yMinValue;
    private String[] mYLabels;
    private List<Float> mDatas;
    private Path mLinePath;
    private Path mFillPath;
    private Paint mPaint;

    public CurveChartView(Context context) {
        this(context, null);
    }

    public CurveChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.yMaxValue = -2.14748365E9F;
        this.yMinValue = 2.14748365E9F;
        this.mDatas = new ArrayList();
        this.initWaveView(context, attrs);
    }

    private void initWaveView(Context context, AttributeSet attrs) {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mLinePath = new Path();
        this.mFillPath = new Path();
        Config config;
        if (attrs == null) {
            config = new Config();
        } else {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.memorymonitor_CurveChartView);
            Config.Builder builder = (new Config.Builder())
                    .setXTextPadding(a.getInteger(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_XTextPadding, 0))
                    .setYTextPadding(a.getInteger(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_YTextPadding, 0))
                    .setMaxValueMulti(a.getFloat(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_MaxValueMulti, 1.2F))
                    .setMinValueMulti(a.getFloat(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_MinValueMulti, 0.8F))
                    .setYPartCount(a.getInteger(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_YPartCount, 5))
                    .setDataSize(a.getInteger(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_DataSize, 30))
                    .setYFormat(a.getString(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_YFormat))
                    .setXYColor(a.getColor(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_XYColor, -7829368))
                    .setXYStrokeWidth(a.getFloat(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_XYStrokeWidth, 2.0F))
                    .setLineColor(a.getColor(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_LineColor, -7829368))
                    .setLineStrokeWidth(a.getFloat(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_LineStrokeWidth, 2.0F))
                    .setFillColor(a.getColor(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_FillColor, Config.DEFAULT_FILL_COLOR))
                    .setYLabelColor(a.getColor(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_YLabelColor, -7829368))
                    .setYLabelSize(a.getFloat(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_YLabelSize, 12.0F))
                    .setGraduatedLineColor(a.getColor(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_GraduatedLineColor, Config.DEFAULT_GRADUATEDLINE_COLOR))
                    .setGraduatedStrokeWidth(a.getFloat(R.styleable.memorymonitor_CurveChartView_memorymonitor_ccv_GraduatedLineStrokeWidth, 1.0F));
            a.recycle();
            config = builder.create();
        }

        this.setUp(config);
    }

    public void setUp(Config config) {
        if (config != null) {
            this.mConfig = config;
            this.mYLabels = new String[this.mConfig.mYPartCount];
            this.mDatas.clear();
        }
    }

    public void addData(float data) {
        this.mDatas.add(data);
        if (this.mDatas.size() > this.mConfig.mDataSize) {
            this.mDatas.remove(0);
        }

        this.prepareData();
        this.postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawXY(canvas);
        int size = this.mDatas.size();
        if (size != 0) {
            this.drawLine(canvas, size);
            this.drawScaleLabel(canvas);
        }
    }

    private void drawXY(Canvas canvas) {
        this.mPaint.reset();
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setColor(this.mConfig.mXYColor);
        this.mPaint.setStrokeWidth(this.mConfig.mXYStrokeWidth);
        canvas.drawLine((float)this.getXPoint(), (float)this.getPaddingTop(), (float)this.getXPoint(), (float)this.getYPoint(), this.mPaint);
        canvas.drawLine((float)this.getXPoint(), (float)this.getYPoint(), (float)(this.getWidth() - this.getPaddingRight()), (float)this.getYPoint(), this.mPaint);
    }

    private void drawScaleLabel(Canvas canvas) {
        this.mPaint.reset();
        this.mPaint.setStyle(Style.STROKE);
        this.createYText();
        int yIntervalLen = this.getYLen() / (this.mConfig.mYPartCount - 1);

        for(int i = 0; i < this.mConfig.mYPartCount; ++i) {
            int scaleY = this.getYPoint() - yIntervalLen * i;
            this.mPaint.setColor(this.mConfig.mGraduatedLineColor);
            this.mPaint.setStrokeWidth(this.mConfig.mGraduatedLineStrokeWidth);
            canvas.drawLine((float)this.getXPoint(), (float)scaleY, (float)(this.getWidth() - this.getPaddingRight()), (float)scaleY, this.mPaint);
            if (!TextUtils.isEmpty(this.mConfig.mYFormat)) {
                this.mPaint.setColor(this.mConfig.mYLabelColor);
                this.mPaint.setStrokeWidth(0.0F);
                this.mPaint.setTextSize(this.mConfig.mYLabelSize);
                canvas.drawText(this.mYLabels[i], (float)this.getPaddingLeft(), (float)scaleY, this.mPaint);
            }
        }

    }

    private void drawLine(Canvas canvas, int size) {
        this.mLinePath.reset();
        this.mFillPath.reset();
        this.mLinePath.moveTo((float)this.getXPoint(), (float)this.getYPoint() - (this.mDatas.get(0) - this.yMinValue) * this.getYLenPerValue());
        this.mFillPath.moveTo((float)this.getXPoint(), (float)this.getYPoint());
        this.mFillPath.lineTo((float)this.getXPoint(), (float)this.getYPoint() - (this.mDatas.get(0) - this.yMinValue) * this.getYLenPerValue());

        for(int i = 1; i < size; ++i) {
            float value = this.mDatas.get(i);
            this.mLinePath.lineTo((float)this.getXPoint() + (float)i * this.getXLenPerCount(), (float)this.getYPoint() - (value - this.yMinValue) * this.getYLenPerValue());
            this.mFillPath.lineTo((float)this.getXPoint() + (float)i * this.getXLenPerCount(), (float)this.getYPoint() - (value - this.yMinValue) * this.getYLenPerValue());
        }

        this.mFillPath.lineTo((float)this.getXPoint() + (float)(size - 1) * this.getXLenPerCount(), (float)this.getYPoint());
        this.mFillPath.close();
        this.mPaint.reset();
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setColor(this.mConfig.mLineColor);
        this.mPaint.setStrokeWidth(this.mConfig.mLineStrokeWidth);
        canvas.drawPath(this.mLinePath, this.mPaint);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(this.mConfig.mFillColor);
        canvas.drawPath(this.mFillPath, this.mPaint);
    }

    private void prepareData() {
        float maxValue = -2.14748365E9F;
        float minValue = 2.14748365E9F;
        int size = this.mDatas.size();

        for(int i = 0; i < size; ++i) {
            float v = this.mDatas.get(i);
            if (v > maxValue) {
                maxValue = v;
            }

            if (v < minValue) {
                minValue = v;
            }
        }

        this.yMaxValue = this.mConfig.mMaxValueMulti * maxValue;
        this.yMinValue = this.mConfig.mMinValueMulti * minValue;
    }

    private void createYText() {
        if (!TextUtils.isEmpty(this.mConfig.mYFormat)) {
            for(int i = 0; i < this.mConfig.mYPartCount; ++i) {
                this.mYLabels[i] = String.format(this.mConfig.mYFormat, (this.yMaxValue - this.yMinValue) * (float)i / (float)(this.mConfig.mYPartCount - 1) + this.yMinValue);
            }

        }
    }

    private float getYLenPerValue() {
        return (float)this.getYLen() / (this.yMaxValue - this.yMinValue);
    }

    private float getXLenPerCount() {
        return (float)this.getXLen() / (float)(this.mConfig.mDataSize - 1);
    }

    private int getXPoint() {
        return this.getPaddingLeft() + this.mConfig.mXTextPadding;
    }

    private int getYPoint() {
        return this.getHeight() - this.getPaddingBottom() - this.mConfig.mYTextPadding;
    }

    private int getXLen() {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight() - this.mConfig.mXTextPadding;
    }

    private int getYLen() {
        return this.getHeight() - this.getPaddingBottom() - this.getPaddingTop() - this.mConfig.mYTextPadding;
    }
}

