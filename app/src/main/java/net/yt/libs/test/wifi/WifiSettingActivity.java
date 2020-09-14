package net.yt.libs.test.wifi;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import net.yt.lib.sdk.base.BaseActivity;
import net.yt.lib.sdk.utils.ToastUtils;
import net.yt.lib.wifi.ActWifi;
import net.yt.lib.wifi.Constant;
import net.yt.lib.wifi.IConnectCallback;
import net.yt.lib.wifi.IScanCallback;
import net.yt.lib.wifi.WifiBean;
import net.yt.lib.wifi.WifiTool;
import net.yt.libs.test.R;

import java.util.ArrayList;
import java.util.List;

public class WifiSettingActivity extends BaseActivity implements View.OnClickListener {
    public final static int RESULT_CODE_WIFI_INFO = 1;
    public final static int RESULT_CODE_WIFI_CURR_INFO = 2;
    public final static int RESULT_CODE_WIFI_PASSWORD_EDIT = 3;

    private final long SCAN_DURING = 10 * 1000L; //扫描间隔

    ImageView mBackIv;
    TextView mTitleTv;
    TextView mNextTv;

    NestedScrollView mAllLy;

    TextView mCurrWifiTip;
    View mCurrWifiLy;
    ImageView mCurrwifiLevel;
    TextView mCurrwifiName;
    TextView mCurrwifiState;
    ImageView mCurrwifiLock;

    ProgressBar mUpdatePb;
    TextView mUpdateTv;

    ListView mWifiListView;
    WifiListAdapter mWifiListAdapter;

    Handler mHandler;
    ScanRunning mScanRunning;

    //要把连接中的wifi提到最前面
    private String mConnectingSSID;

    private boolean mFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wifi_setting2);

        mHandler = new Handler();
        mScanRunning = new ScanRunning();

        View titileLy = $(R.id.ly_title);
        //让标题获取焦点,防止listview刷新后自动滚动到listview区域
        titileLy.setFocusable(true);
        titileLy.setFocusableInTouchMode(true);
        titileLy.requestFocus();

        mBackIv = titileLy.findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(this);
        mTitleTv = titileLy.findViewById(R.id.tv_title);
        mTitleTv.setText("WIFI设置");
        mNextTv = titileLy.findViewById(R.id.tv_next);

        mAllLy = findViewById(R.id.nsl_all_ly);

        mCurrWifiTip = $(R.id.tv_curr_wifi);
        mCurrWifiLy = $(R.id.ly_curr_wifi);
        mCurrWifiLy.setOnClickListener(this);
        mCurrwifiLevel = mCurrWifiLy.findViewById(R.id.img_wifi_level);
        mCurrwifiName = mCurrWifiLy.findViewById(R.id.tv_wifi_name);
        mCurrwifiState = mCurrWifiLy.findViewById(R.id.tv_wifi_state);
        mCurrwifiLock = mCurrWifiLy.findViewById(R.id.img_wifi_lock);

        mUpdatePb = $(R.id.pb_update_wifi);
        mUpdatePb.setOnClickListener(this);
        mUpdateTv = $(R.id.tv_update_wifi);
        mUpdateTv.setOnClickListener(this);

        mWifiListView = $(R.id.list_wifi_scan);
        mWifiListAdapter = new WifiListAdapter(LayoutInflater.from(WifiSettingActivity.this));
        mWifiListView.setAdapter(mWifiListAdapter);
        mWifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                scanListOnclick((WifiBean)mWifiListAdapter.getItem(position));
            }
        });

        WifiTool.I().setConnectStateCallback(new WifiTool.IConnectStateCallback() {
            @Override
            public void onChange() {
                updateUI();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        WifiTool.I().clearConnectStateCallback();
    }

    @Override
    public void onResume(){
        super.onResume();
        net.yt.lib.log.L.d("onResume ... ");
        //进来就扫描,第一次要立即扫描
        if(mFirstIn) {
            mFirstIn = false;
            startScan(0L);
        }else{
            startScan(SCAN_DURING);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        net.yt.lib.log.L.d("onPause ... ");
        stopAutoScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == RESULT_CODE_WIFI_INFO) {
            String ssid = data.getStringExtra("SSID");
            int menu = data.getIntExtra("menu", -1);
            //连接网络 0, 取消保存 1
            if(0 == menu){
                doConnect(ssid, null, -1);
                updateUI();
            }else if(1 == menu){
                WifiTool.I().cancelWifi(ssid);
                updateUI();
            }
        }else if (requestCode == RESULT_CODE_WIFI_CURR_INFO) {
            WifiTool.I().ignoreConnectedWifi();
            updateUI();
        }else if(requestCode == RESULT_CODE_WIFI_PASSWORD_EDIT){
            String ssid = data.getStringExtra("SSID");
            String password = data.getStringExtra("password");
            int security = data.getIntExtra("security", -1);
            doConnect(ssid, password, security);
            updateUI();
        }
    }

    private class ScanRunning implements Runnable{
        @Override
        public void run() {
            net.yt.lib.log.L.d("ScanRunning start ... ");
            mUpdatePb.setVisibility(View.VISIBLE);
            WifiTool.I().scan(new IScanCallback() {
                @Override
                public void onSucess(List<WifiBean> result) {
                    updateUI();
                    mUpdatePb.setVisibility(View.INVISIBLE);
                    if(null != mHandler) {
                        mHandler.removeCallbacks(ScanRunning.this);
                        mHandler.postDelayed(ScanRunning.this, SCAN_DURING);
                    }
                }

                @Override
                public void onFail(String error) {
                    updateUI();
                    mUpdatePb.setVisibility(View.INVISIBLE);
                    if(null != mHandler) {
                        mHandler.removeCallbacks(ScanRunning.this);
                        mHandler.postDelayed(ScanRunning.this, SCAN_DURING);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                WifiSettingActivity.this.finish();
                break;

            case R.id.ly_curr_wifi:
                ActWifi currWifi = WifiTool.I().getConnectedWifi();
                if(currWifi != null) {
                    Intent intent = new Intent(WifiSettingActivity.this, WifiCurrInfoActivity.class);
                    intent.putExtra("SSID", currWifi.SSID);
                    intent.putExtra("IP", WifiTool.I().getCurrIP());
                    intent.putExtra("MAC", WifiTool.I().getCurrMac());
                    intent.putExtra("GATEWAY", WifiTool.I().getCurrGateway());
                    intent.putExtra("MARK", WifiTool.getCurrIpAddrMask());
                    intent.putExtra("DNS", WifiTool.I().getCurrDNS());
                    startActivityForResult(intent, RESULT_CODE_WIFI_CURR_INFO);
                }
                break;

            case R.id.pb_update_wifi:
            case R.id.tv_update_wifi:
                startScan(0L);
                break;
        }
    }

    private void startScan(long delayTime){
        if(null != mHandler) {
            mHandler.removeCallbacks(mScanRunning);
            mHandler.postDelayed(mScanRunning, delayTime);
        }
    }

    private void stopAutoScan(){
        if(null != mHandler) {
            mHandler.removeCallbacks(mScanRunning);
        }
    }

    private void updateUI(){
        ActWifi currWifi = WifiTool.I().getConnectedWifi();
        String currSSID = null;
        if(currWifi != null){
            mCurrWifiTip.setVisibility(View.VISIBLE);
            mCurrWifiLy.setVisibility(View.VISIBLE);
            int rssiLevel = currWifi.level;
            if(0 == rssiLevel) {
                mCurrwifiLevel.setImageResource(R.mipmap.icon_wifi_level_1);
            }else if(1 == rssiLevel) {
                mCurrwifiLevel.setImageResource(R.mipmap.icon_wifi_level_1);
            }else if(2 == rssiLevel) {
                mCurrwifiLevel.setImageResource(R.mipmap.icon_wifi_level_2);
            }else if(3 == rssiLevel) {
                mCurrwifiLevel.setImageResource(R.mipmap.icon_wifi_level_3);
            }else if(4 == rssiLevel) {
                mCurrwifiLevel.setImageResource(R.mipmap.icon_wifi_level_4);
            }
            currSSID = currWifi.SSID;
            mCurrwifiName.setText(currSSID);
            mCurrwifiState.setVisibility(View.VISIBLE);
            mCurrwifiState.setText("已连接");
            mCurrwifiLock.setVisibility(View.VISIBLE);
            mCurrwifiLock.setImageResource(R.mipmap.icon_wifi_connected);
        }else{
            mCurrWifiTip.setVisibility(View.GONE);
            mCurrWifiLy.setVisibility(View.GONE);
        }

        List<WifiBean> scanlist = new ArrayList<WifiBean>();
        scanlist.clear();
        List<WifiBean> noSaveScanlist = new ArrayList<WifiBean>();
        List<WifiBean> saveScanlist = new ArrayList<WifiBean>();
        WifiBean connectingBean = null;
        for(WifiBean bean : WifiTool.I().getLastScanResult()){
            //把已连接的剔除出去
            if(!TextUtils.isEmpty(currSSID)
                    && !TextUtils.isEmpty(bean.SSID)
                    && bean.SSID.equals(currSSID)){
                continue;
            }

            //处理连接中
            if(!TextUtils.isEmpty(bean.SSID)
                    && !TextUtils.isEmpty(WifiTool.I().getConnnectingSSID())
                    && bean.SSID.equals(WifiTool.I().getConnnectingSSID())){
                bean.state = WifiBean.State.CONNECTING;
                connectingBean = bean.copy();
                continue;
            }

            //处理其他状态
            if(WifiTool.I().isSsidSaved(bean.SSID) == WifiTool.SSID_STATE.SSID_STATE_EXIST_BUT_DISCONNECTED){
                bean.state = WifiBean.State.SAVE_DISABLE;
                saveScanlist.add(bean.copy());
            }else if(WifiTool.I().isSsidSaved(bean.SSID) == WifiTool.SSID_STATE.SSID_STATE_EXIST_AND_CONNECTED){
                bean.state = WifiBean.State.SAVE_ENABLE;
                saveScanlist.add(bean.copy());
            }else{
                bean.state = WifiBean.State.NONE;
                noSaveScanlist.add(bean.copy());
            }
        }

        //先加未保存再加已保存
        scanlist.addAll(saveScanlist);
        scanlist.addAll(noSaveScanlist);

        //把连接中放第一个
        if(null != connectingBean) {
            scanlist.add(0, connectingBean);
        }
        
        net.yt.lib.log.L.e("%%%%%%%%%%%%% updateui size = " + scanlist.size());
        mWifiListAdapter.updateData(scanlist);
    }

    private void doConnect(String SSID, String password, int security){
        WifiTool.I().connect(SSID, password, security, new IConnectCallback() {
            @Override
            public void onSucess() {
                ToastUtils.showShortToast("连接"+ SSID + "成功");
                mWifiListAdapter.setConnectingSSID(null);
                mConnectingSSID = null;
                updateUI();
            }

            @Override
            public void onFail(String error) {
                ToastUtils.showShortToast("连接"+ SSID + "失败, " + error);
                mWifiListAdapter.setConnectingSSID(null);
                mConnectingSSID = null;
                updateUI();
            }
        });
        mWifiListAdapter.setConnectingSSID(SSID);
        mConnectingSSID = SSID;
        //滚动到前端
        mAllLy.scrollTo(0, 0);
    }

    private void scanListOnclick(WifiBean bean){
        if(WifiTool.I().isSsidSaved(bean.SSID) != WifiTool.SSID_STATE.SSID_STATE_NO_EXIST){//如果是已经保存，可以选择连接和取消保存
            Intent intent = new Intent(WifiSettingActivity.this, WifiInfoActivity.class);
            intent.putExtra("SSID", bean.SSID);
            startActivityForResult(intent, RESULT_CODE_WIFI_INFO);
        }else if(Constant.SECURITY_NONE == bean.security){ //如果是没有号码，直接连接
            doConnect(bean.SSID, null,  bean.security);
        }else { //需要输入密码
            Intent intent = new Intent(WifiSettingActivity.this, WifiPasswordEditActivity.class);
            intent.putExtra("SSID", bean.SSID);
            intent.putExtra("security", bean.security);
            startActivityForResult(intent, RESULT_CODE_WIFI_PASSWORD_EDIT);
        }
    }
}