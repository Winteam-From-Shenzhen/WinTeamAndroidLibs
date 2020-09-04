package net.yt.libs.test;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import net.yt.lib.log.L;
import net.yt.lib.push.IOperateCallback;
import net.yt.lib.push.IPushReceiver;
import net.yt.lib.push.Message;
import net.yt.lib.push.Push;
import net.yt.lib.push.Target;
import net.yt.lib.sdk.base.BaseApplication;
import net.yt.lib.sdk.utils.ToastUtils;
import net.yt.lib.wifi.WifiTool;

public class App extends BaseApplication {

    Handler mHandler;

    @Override
    public void onCreate(){
        super.onCreate();
        mHandler = new Handler();
        Push.I().init(this, Target.JPUSH, true, new IPushReceiver() {
            @Override
            public void onReceiveNotification(Context context, final Message msg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.this, "来新通知了 " + msg.toString(), Toast.LENGTH_LONG).show();
                        //ToastUtils.showLongToast(msg.toString());
                    }
                });

            }

            @Override
            public void onReceiveNotificationClick(Context context, final Message msg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.this, "你点击了通知 " + msg.toString(), Toast.LENGTH_LONG).show();
                        //ToastUtils.showLongToast(msg.toString());
                    }
                });
            }

            @Override
            public void onReceiveMessage(Context context, final Message msg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.this, "透传消息 " + msg.toString(), Toast.LENGTH_LONG).show();
                        //ToastUtils.showLongToast(msg.toString());
                    }
                });
            }
        });

        WifiTool.I().init(this);

        /*
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Push.I().setAlias("95527", new IOperateCallback() {
                    @Override
                    public void sucess() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(App.this, "设置95527成功", Toast.LENGTH_LONG).show();
                                //ToastUtils.showLongToast(msg.toString());
                            }
                        });
                    }

                    @Override
                    public void fail() {
                        android.util.Log.i("YTZN", "***************** set alias fail ");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                android.util.Log.i("YTZN", "***************** set alias faillllllllllll ");
                                Toast.makeText(App.this, "设置95527失败123456789 " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                                //ToastUtils.showLongToast(msg.toString());
                            }
                        });
                    }
                });
            }
        }, 5000L);
        android.util.Log.i("YTZN", "***************** set alias beginnnnnnnnnnnnnnn ");

        /*
        Push.I().setTags("10086,10000", new IOperateCallback() {
            @Override
            public void sucess() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.this, "设置标签成功", Toast.LENGTH_LONG).show();
                        //ToastUtils.showLongToast(msg.toString());
                    }
                });
            }

            @Override
            public void fail() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.this, "设置标签失败", Toast.LENGTH_LONG).show();
                        //ToastUtils.showLongToast(msg.toString());
                    }
                });
            }
        });
        */
    }

}
