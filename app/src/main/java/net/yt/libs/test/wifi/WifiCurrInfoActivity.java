package net.yt.libs.test.wifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.yt.libs.test.R;

public class WifiCurrInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private String mSSID;
    private String mIP;
    private String mMAC;
    private String mGATEWAY;
    private String mMARK;
    private String mDNS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wifi_curr_info);

        mSSID = getIntent().getStringExtra("SSID");
        if(TextUtils.isEmpty(mSSID)){
            finish();
        }
        mIP = getIntent().getStringExtra("IP");
        mMAC = getIntent().getStringExtra("MAC");
        mGATEWAY = getIntent().getStringExtra("GATEWAY");
        mMARK = getIntent().getStringExtra("MARK");
        mDNS = getIntent().getStringExtra("DNS");

        ImageView backIv = findViewById(R.id.back);
        backIv.setOnClickListener(this);
        TextView title = findViewById(R.id.title);
        title.setText(mSSID);
        TextView ignoreTv = findViewById(R.id.tv_ignore);
        ignoreTv.setText("忽略此网络");
        ignoreTv.setOnClickListener(this);
        TextView ipTv = findViewById(R.id.tv_ip);
        ipTv.setText(handlerItemText("IP地址", mIP));
        TextView markTv = findViewById(R.id.tv_mark);
        markTv.setText(handlerItemText("子网掩码", mMARK));
        TextView macTv = findViewById(R.id.tv_mac);
        macTv.setText(handlerItemText("MAC地址", mMAC));
        TextView gatewayTv = findViewById(R.id.tv_gateway);
        gatewayTv.setText(handlerItemText("网关", mGATEWAY));
        TextView dnsTv = findViewById(R.id.tv_dns);
        dnsTv.setText(handlerItemText("DNS", mDNS));
    }

    /**
     * 处理item的标题与内容，并返回
     *
     * @param title   标题
     * @param content 内容
     * @return 返回String组合
     */
    private SpannableString handlerItemText(@NonNull String title, String content) {
        String itemString = title + "\n" + content;
        SpannableString spannableString = new SpannableString(itemString);
        ForegroundColorSpan whiteColorSpan = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));
        ForegroundColorSpan grayColorSpan = new ForegroundColorSpan(Color.parseColor("#A6A6A6"));
        if (!TextUtils.isEmpty(title)) {
            spannableString.setSpan(whiteColorSpan, 0, title.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(content)) {
            spannableString.setSpan(grayColorSpan, title.length(), itemString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.tv_ignore:
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finish();
                break;
        }
    }
}