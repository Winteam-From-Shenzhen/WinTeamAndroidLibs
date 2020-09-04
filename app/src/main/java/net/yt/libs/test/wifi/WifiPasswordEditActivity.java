package net.yt.libs.test.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.yt.lib.sdk.base.BaseActivity;
import net.yt.lib.sdk.utils.ToastUtils;
import net.yt.lib.wifi.Constant;
import net.yt.libs.test.R;

public class WifiPasswordEditActivity extends BaseActivity implements View.OnClickListener {
    private String mSSID;
    private int mSecurity;
    private KeyInputView mInputEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wifi_password_edit);

        mSSID = getIntent().getStringExtra("SSID");
        mSecurity = getIntent().getIntExtra("security", -1);
        if(TextUtils.isEmpty(mSSID)
                || mSecurity < Constant.SECURITY_NONE
                || mSecurity > Constant.SECURITY_EAP){
            finish();
        }

        ImageView backImg = findViewById(R.id.iv_back);
        backImg.setOnClickListener(this);
        TextView title = findViewById(R.id.tv_title);
        title.setText("连接网络");
        TextView wifiNameTv = findViewById(R.id.tv_wifi_name);
        wifiNameTv.setText(mSSID);
        mInputEdt = findViewById(R.id.pwd_edt);
        mInputEdt.setInputStyle(true, true);
        mInputEdt.setCurrentShowInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mInputEdt.setUseDigits(false);
        mInputEdt.setMaxLength(18);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);
        Button connectBtn = findViewById(R.id.connectionBtn);
        connectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.cancelBtn:
                finish();
                break;

            case R.id.connectionBtn:
                String password = mInputEdt.getText().toString();
                if(TextUtils.isEmpty(password)){
                    ToastUtils.showShortToast("请输入密码");
                    break;
                }else if(password.length() < 8){
                    ToastUtils.showShortToast("您输入的密码需要至少8位");
                    break;
                }

                Intent i = new Intent();
                i.putExtra("SSID", mSSID);
                i.putExtra("password", password);
                i.putExtra("security", mSecurity);
                setResult(Activity.RESULT_OK, i);
                finish();
                break;
        }
    }
}