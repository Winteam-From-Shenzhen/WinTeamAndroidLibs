package net.yt.libs.test.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.yt.lib.sdk.base.BaseActivity;
import net.yt.libs.test.R;

public class WifiInfoActivity extends BaseActivity implements View.OnClickListener {
    private String mSSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wifi_info);

        mSSID = getIntent().getStringExtra("SSID");
        if(TextUtils.isEmpty(mSSID)){
            finish();
        }

        ImageView backIv = findViewById(R.id.back);
        backIv.setOnClickListener(this);
        TextView title = findViewById(R.id.title);
        title.setText(mSSID);
        TextView connect = findViewById(R.id.tv_connect);
        connect.setText("连接网络");
        connect.setOnClickListener(this);
        TextView ignore = findViewById(R.id.tv_ignore);
        ignore.setText("取消保存");
        ignore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.tv_connect:
                Intent i = new Intent();
                i.putExtra("SSID", mSSID);
                i.putExtra("menu", 0);
                setResult(Activity.RESULT_OK, i);
                finish();
                break;

            case R.id.tv_ignore:
                Intent i2 = new Intent();
                i2.putExtra("SSID", mSSID);
                i2.putExtra("menu", 1);
                setResult(Activity.RESULT_OK, i2);
                finish();
                break;
        }
    }

}