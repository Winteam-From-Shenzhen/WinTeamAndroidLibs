package net.yt.libs.test;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import net.yt.lib.push.IOperateCallback;
import net.yt.lib.push.Push;
import net.yt.lib.sdk.base.BaseActivity;

public class PushActivity extends BaseActivity implements View.OnClickListener {

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        mHandler = new Handler();

        $(R.id.btn_set_alias).setOnClickListener(this);
        $(R.id.btn_clear_alias).setOnClickListener(this);
        $(R.id.btn_set_tags).setOnClickListener(this);
        $(R.id.btn_clear_tags).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_set_alias){
            Push.I().setAlias("95527", new IOperateCallback() {
                @Override
                public void sucess() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** set alias sucesssssssssss ");
                            Toast.makeText(PushActivity.this, "设置95527成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void fail() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** set alias faillllllllllll ");
                            Toast.makeText(PushActivity.this, "设置95527失败123456789 " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }else if(v.getId() == R.id.btn_clear_alias){
            Push.I().clearAlias(new IOperateCallback() {
                @Override
                public void sucess() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** clear alias sucesssssssssss ");
                            Toast.makeText(PushActivity.this, "清除别名成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void fail() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** clear alias faillllllllllll ");
                            Toast.makeText(PushActivity.this, "清除别名失败 " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }else if(v.getId() == R.id.btn_set_tags){
            Push.I().setTags("10000,10086", new IOperateCallback() {
                @Override
                public void sucess() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** set tag sucesssssssssss ");
                            Toast.makeText(PushActivity.this, "设置标签10000 10086成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void fail() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** set tag faillllllllllll ");
                            Toast.makeText(PushActivity.this, "设置标签10000 10086失败 " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }else if(v.getId() == R.id.btn_clear_tags){
            Push.I().clearTags(new IOperateCallback() {
                @Override
                public void sucess() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** clear tag sucesssssssssss ");
                            Toast.makeText(PushActivity.this, "清除标签成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void fail() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.i("YTZN", "***************** clear tags faillllllllllll ");
                            Toast.makeText(PushActivity.this, "清除标签失败 " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }
}