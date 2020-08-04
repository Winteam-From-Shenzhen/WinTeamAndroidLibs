package net.yt.lib.memorymonitor;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class FloatContainer {
    private static final String TAG = "FloatContainer";
    private WindowManager mWm;
    private LayoutParams mLp;
    private View mContentView;

    public FloatContainer(Context context) {
        this.mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        this.mLp = new LayoutParams();
    }

    public void release() {
        if (this.mContentView != null && this.mContentView.getParent() != null) {
            this.mWm.removeView(this.mContentView);
        }
    }

    public void attachToWindow(View view, int gravity, int x, int y, int width, int height) {
        if (view.getParent() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.mLp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                this.mLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            this.mLp.format = PixelFormat.TRANSLUCENT;
            this.mLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            this.mLp.gravity = gravity;
            this.mLp.width = width == 0 ? -2 : width;
            this.mLp.height = height == 0 ? -2 : height;
            this.mLp.x = x;
            this.mLp.y = y;

            try {
                this.mContentView = view;
                this.mWm.addView(this.mContentView, this.mLp);
            } catch (Exception var8) {
                Log.d("FloatContainer", "悬浮窗添加失败:" + var8.getLocalizedMessage());
            }

        }
    }
}
