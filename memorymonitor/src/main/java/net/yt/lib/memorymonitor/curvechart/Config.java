package net.yt.lib.memorymonitor.curvechart;


import android.graphics.Color;

public class Config {

    public static final int DEFAULT_X_TEXT_PADDING = 0;
    public static final int DEFAULT_Y_TEXT_PADDING = 0;
    public static final float DEFAULT_Y_MAX_MULTIPLE = 1.2F;
    public static final float DEFAULT_Y_MIN_MULTIPLE = 0.8F;
    public static final int DEFAULT_Y_PART_COUNT = 5;
    public static final int DEFAULT_DATA_SIZE = 30;
    public static final String DEFAULT_Y_FORMAT = "%.1f";
    public static final int DEFAULT_XY_COLOR = -7829368;
    public static final float DEFAULT_XY_STROKE_WIDTH = 2.0F;
    public static final int DEFAULT_LINE_COLOR = -7829368;
    public static final float DEFAULT_LINE_STROKE_WIDTH = 2.0F;
    public static final int DEFAULT_FILL_COLOR = Color.parseColor("#aa0000FF");
    public static final int DEFAULT_Y_LABEL_COLOR = -7829368;
    public static final int DEFAULT_Y_LABEL_SIZE = 12;
    public static final int DEFAULT_GRADUATEDLINE_COLOR = Color.parseColor("#bbbbbb");
    public static final float DEFAULT_GRADUATEDLINE_STROKE_WIDTH = 1.0F;

    public int mGraduatedLineColor = DEFAULT_GRADUATEDLINE_COLOR;
    public float mGraduatedLineStrokeWidth = DEFAULT_GRADUATEDLINE_STROKE_WIDTH;
    public int mYLabelColor = DEFAULT_Y_LABEL_COLOR;
    public float mYLabelSize = DEFAULT_Y_LABEL_SIZE;
    public int mXTextPadding = DEFAULT_X_TEXT_PADDING;
    public int mYTextPadding = DEFAULT_Y_TEXT_PADDING;
    public float mMaxValueMulti = DEFAULT_Y_MAX_MULTIPLE;
    public float mMinValueMulti = DEFAULT_Y_MIN_MULTIPLE;
    public int mYPartCount = DEFAULT_Y_PART_COUNT;
    public int mDataSize = DEFAULT_DATA_SIZE;
    public String mYFormat = DEFAULT_Y_FORMAT;
    public int mXYColor = DEFAULT_XY_COLOR;
    public float mXYStrokeWidth = DEFAULT_XY_STROKE_WIDTH;
    public int mLineColor = DEFAULT_LINE_COLOR ;
    public float mLineStrokeWidth = DEFAULT_LINE_STROKE_WIDTH;
    public int mFillColor = DEFAULT_FILL_COLOR;

    public Config() {
    }

    public static class Builder {
        private Config mConfig = new Config();

        public Builder() {
        }

        public Config.Builder setXTextPadding(int XTextPadding) {
            this.mConfig.mXTextPadding = XTextPadding;
            return this;
        }

        public Config.Builder setYTextPadding(int YTextPadding) {
            this.mConfig.mYTextPadding = YTextPadding;
            return this;
        }

        public Config.Builder setMaxValueMulti(float maxValueMulti) {
            this.mConfig.mMaxValueMulti = maxValueMulti;
            return this;
        }

        public Config.Builder setMinValueMulti(float minValueMulti) {
            this.mConfig.mMinValueMulti = minValueMulti;
            return this;
        }

        public Config.Builder setYPartCount(int YPartCount) {
            this.mConfig.mYPartCount = YPartCount;
            return this;
        }

        public Config.Builder setDataSize(int dataSize) {
            this.mConfig.mDataSize = dataSize;
            return this;
        }

        public Config.Builder setYFormat(String format) {
            this.mConfig.mYFormat = format;
            return this;
        }

        public Config.Builder setXYColor(int XYColor) {
            this.mConfig.mXYColor = XYColor;
            return this;
        }

        public Config.Builder setXYStrokeWidth(float XYStrokeWidth) {
            this.mConfig.mXYStrokeWidth = XYStrokeWidth;
            return this;
        }

        public Config.Builder setLineColor(int lineColor) {
            this.mConfig.mLineColor = lineColor;
            return this;
        }

        public Config.Builder setLineStrokeWidth(float lineStrokeWidth) {
            this.mConfig.mLineStrokeWidth = lineStrokeWidth;
            return this;
        }

        public Config.Builder setFillColor(int fillColor) {
            this.mConfig.mFillColor = fillColor;
            return this;
        }

        public Config.Builder setYLabelColor(int yLabelColor) {
            this.mConfig.mYLabelColor = yLabelColor;
            return this;
        }

        public Config.Builder setYLabelSize(float yLabelSize) {
            this.mConfig.mYLabelSize = yLabelSize;
            return this;
        }

        public Config.Builder setGraduatedLineColor(int graduatedLineColor) {
            this.mConfig.mGraduatedLineColor = graduatedLineColor;
            return this;
        }

        public Config.Builder setGraduatedStrokeWidth(float graduatedLineStrokeWidth) {
            this.mConfig.mGraduatedLineStrokeWidth = graduatedLineStrokeWidth;
            return this;
        }

        public Config create() {
            return this.mConfig;
        }
    }
}
