package net.yt.lib.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;

import net.yt.lib.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiTool {
    private static final long SCAN_TIMEOUT = 6 * 1000L;
    private static final long CONNECT_TIMEOUT = 30 * 1000L;

    private WifiManager mWifiManager;
    private WiFiReceiver mWiFiReceiver;
    private Context mContext;
    private Handler mMainHandler;
    private static WifiTool sInstance;

    private final Object mScanLocker = new Object();
    private volatile boolean mIsScanning = false;
    private ScanRunning mScanRunning;
    private IScanCallback mScanCallback;

    private final Object mConnectLocker = new Object();
    private volatile boolean mIsConnecting = false;
    private ConnectRunning mConnectRunning;
    private IConnectCallback mConnectCallback;
    private String mConnectSSID; //主动去连接的wifi
    private String mConnectingSSID; //正在连接的wifi
    private enum CONNECT_STATE{
        DISCONNECT, CONNECTING, CONNECTED
    }
    private volatile CONNECT_STATE mConnectState = CONNECT_STATE.DISCONNECT; //判断当前wifi连接状态

    private  List<WifiBean> mCacheScanWifi = new ArrayList<WifiBean>();

    public interface IConnectStateCallback{
        void onChange();
    }

    private IConnectStateCallback mConnectStateCallback;

    private WifiTool(){
    }

    public static WifiTool I(){
        if(null == sInstance){
            synchronized (WifiTool.class){
                if(null == sInstance){
                    sInstance = new WifiTool();
                }
            }
        }
        return sInstance;
    }

    /*
     * 初始化，需要在所有操作之前完成
     */
    public void init(Context context){
        synchronized (WifiTool.class) {
            mContext = context;
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mMainHandler = new Handler(Looper.getMainLooper());
            mScanRunning = new ScanRunning();
            mConnectRunning = new ConnectRunning();

            mWiFiReceiver = new WiFiReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//WiFi状态变化
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//WiFi开关状态
            intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(mWiFiReceiver, intentFilter);
        }
    }

    /*
     * 反初始化
     */
    public void unInit(){
        synchronized (WifiTool.class) {
            mWifiManager = null;
            mContext.unregisterReceiver(mWiFiReceiver);
            mWiFiReceiver = null;
            mContext = null;

            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;

            mConnectStateCallback = null;
        }
    }

    private void checkInit(){
        if(null == mContext){
            throw new IllegalArgumentException("WifiTool need init first ... ");
        }
    }

    /*
     * 监听当前网络连接状态
     */
    public void setConnectStateCallback(IConnectStateCallback cb){
        checkInit();
        mConnectStateCallback = cb;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if(null != mConnectStateCallback){
                    mConnectStateCallback.onChange();
                }
            }
        });
    }

    /*
     * 反注销监听当前网络连接状态，防止内存泄漏
     */
    public void clearConnectStateCallback(){
        checkInit();
        mConnectStateCallback = null;
    }

    /*
     * 判断当前网络是否链接
     */
    public boolean isWifiEnabled(){
        checkInit();
        return mWifiManager.isWifiEnabled();
    }

    /*
     * 打开关闭wifi开关
     */
    public void setWifiEnabled(boolean enable){
        checkInit();
        mWifiManager.setWifiEnabled(enable);
    }

    /*
     * 开始扫描
     */
    public void scan(final IScanCallback cb){
        synchronized (mScanLocker) {
            checkInit();
            if(mIsScanning){
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cb.onFail("正在扫描中");
                    }
                });
                return;
            }
            mIsScanning = true;
            mScanCallback = cb;
            mWifiManager.startScan();
            mMainHandler.removeCallbacks(mScanRunning);
            mMainHandler.postDelayed(mScanRunning, SCAN_TIMEOUT);
        }
    }

    private class ScanRunning implements Runnable {
        @Override
        public void run() {
            if(mIsScanning) {
                L.w("Scan time out ... ");
            }
            doScanResult();
        }
    }

    private void doScanResult(){
        synchronized (mScanLocker) {
            if(mIsScanning) {
                mIsScanning = false;
                mMainHandler.removeCallbacks(mScanRunning);
                if (null != mScanCallback) {
                    List<ScanResult> srList = mWifiManager.getScanResults();
                    net.yt.lib.log.L.e("%%%%%%%%%%%%% updateui-1 size = " + srList.size());
                    synchronized (mCacheScanWifi) {
                        List<WifiBean> scanListTemp = new ArrayList<WifiBean>();
                        for (ScanResult sr : srList) {
                            WifiBean wb = new WifiBean();
                            wb.SSID = WifiHelper.removeDoubleQuotes(sr.SSID);
                            wb.BSSID = WifiHelper.removeDoubleQuotes(sr.BSSID);
                            wb.frequency = sr.frequency;
                            wb.refreshTime = sr.timestamp;
                            wb.security = WifiHelper.securityType(sr.capabilities);
                            wb.level = WifiHelper.formatRssi(sr.level);

                            //去空名
                            if (TextUtils.isEmpty(wb.SSID)) {
                                L.e("scan and some ssid is empty " + wb.toString());
                                continue;
                            }

                            //去重
                            boolean theSame = false;
                            for (WifiBean bean : scanListTemp) {
                                if (!TextUtils.isEmpty(bean.SSID) && bean.SSID.equals(wb.SSID)) {
                                    theSame = true;
                                    break;
                                }
                            }
                            if (theSame) {
                                L.e("scan and some ssid is the same, SSID = " + wb.SSID + " BSSID = " + wb.BSSID);
                                continue;
                            }

                            scanListTemp.add(wb);
                        }

                        //android6.0没有帮忙按照信号强度排列
                        if(!Utils.isOSVersionNougat()){
                            Collections.sort(scanListTemp, new Comparator<WifiBean>() {
                                @Override
                                public int compare(WifiBean u1, WifiBean u2) {
                                    return u2.level - u1.level;
                                }
                            });
                        }

                        //添加
                        mCacheScanWifi.clear();
                        mCacheScanWifi.addAll(scanListTemp);
                    }
                    mScanCallback.onSucess(mCacheScanWifi);
                    mScanCallback = null;
                }
            }else{
                L.w("doScanResult but mIsScanning is false");
            }
        }
    }

    /*
     * 获取上次扫描的结果
     */
    public List<WifiBean> getLastScanResult(){
        checkInit();
        synchronized (mCacheScanWifi) {
            ArrayList<WifiBean> result = new ArrayList<WifiBean>();
            result.addAll(mCacheScanWifi);
            net.yt.lib.log.L.e("%%%%%%%%%%%%% updateui0 size = " + result.size());
            return result;
        }
    }

    /*
     * 开始连接
     */
    public void connect(String ssid, String password, int security, final IConnectCallback cb){
        synchronized (mConnectLocker) {
            checkInit();
            if(mIsConnecting){
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cb.onFail("正在连接中");
                    }
                });
                return;
            }

            //如果要连接的wifi已经保存过，就直接连接
            WifiConfiguration tempConfig = WifiHelper.isExsits(mWifiManager, ssid);
            if(tempConfig != null) {
                //禁掉所有已经保存过的wifi,暂时不禁止，这样就不会自动连接
                //for (WifiConfiguration c : mWifiManager.getConfiguredNetworks()) {
                //    mWifiManager.disableNetwork(c.networkId);
                //}
                //mWifiManager.disconnect();
                boolean bRet = mWifiManager.enableNetwork(tempConfig.networkId, true);
                //mWifiManager.reconnect();
                L.d("WifiTool " + ssid + " enable network " + bRet);
            }else {
                //创建连接的config
                WifiConfiguration wifiConfig = WifiHelper.createWifiConfiguration(mWifiManager, ssid, password, security);
                if(wifiConfig == null) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            cb.onFail("暂不支持此种网络");
                        }
                    });
                    return;
                }
                int netID = mWifiManager.addNetwork(wifiConfig);
                boolean bRet = mWifiManager.enableNetwork(netID, true); //这个参数需要验证下
                L.d("WifiTool " + ssid + " enable network2 " + bRet);
            }

            mIsConnecting = true;
            mConnectCallback = cb;
            mConnectSSID = ssid;

            mMainHandler.removeCallbacks(mConnectRunning);
            mMainHandler.postDelayed(mConnectRunning, CONNECT_TIMEOUT);
        }
    }

    private class ConnectRunning implements Runnable{
        @Override
        public void run() {
            synchronized (mConnectLocker) {
                if(mIsConnecting) {
                    L.w("WifiTool connect is timeout " + mConnectSSID);
                    mIsConnecting = false;
                    if (null != mConnectCallback) {
                        mConnectCallback.onFail("连接超时");
                        mConnectCallback = null;
                    }
                }
            }
        }
    }

    /*
     * 获取当前连接状态
     */
    public ActWifi getConnectedWifi(){
        checkInit();
        if(mConnectState != CONNECT_STATE.CONNECTED){
            return null;
        }
        if(!Utils.isOSVersionNougat()){
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if(existingConfig.status == WifiConfiguration.Status.CURRENT) {
                    ActWifi result = new ActWifi();
                    result.SSID = WifiHelper.removeDoubleQuotes(existingConfig.SSID);
                    WifiInfo wifiInfo = WifiHelper.getConnectionInfo(mWifiManager);
                    if(wifiInfo != null){
                        result.level = WifiHelper.formatRssi(wifiInfo.getRssi());
                        L.d("getConnectedWifi result SSID = " + result.SSID + " WifiInfo SSID = " + wifiInfo.getSSID());
                    }else{
                        result.level = 3; //如果没有，默认3
                    }
                    return result;
                }
            }
            return null;
        }else{
            WifiInfo wifiInfo = WifiHelper.getConnectionInfo(mWifiManager);
            if(null != wifiInfo) {
                L.d(" getConnectionInfo " + wifiInfo.toString());
                ActWifi result = new ActWifi();
                result.SSID = WifiHelper.removeDoubleQuotes(wifiInfo.getSSID());
                result.level = WifiHelper.formatRssi(wifiInfo.getRssi());
                return result;
            }
            return null;
        }
    }

    public String getConnnectingSSID(){
        checkInit();
        return mConnectingSSID;
    }

    /*
     * 获取当前的IP地址
     */
    public String getCurrIP(){
        checkInit();
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return WifiHelper.intToIp(wifiInfo.getIpAddress());
    }

    /*
     * 获取当前连接的DNS
     */
    public String getCurrDNS(){
        checkInit();
        if(mConnectState != CONNECT_STATE.CONNECTED){
            return null;
        }
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        return WifiHelper.intToIp(dhcpInfo.dns1);
    }

    /*
     * 获取当前连接的网关
     */
    public String getCurrGateway(){
        checkInit();
        if(mConnectState != CONNECT_STATE.CONNECTED){
            return null;
        }
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        return WifiHelper.intToIp(dhcpInfo.gateway);
    }

    /*
     * 获取当前WIFI的mac地址
     */
    public String getCurrMac() {
        checkInit();
        return WifiHelper.getMacFromHardware(mContext);
    }

    /*
     * 获取当前WIFI掩码地址
     */
    public static String getCurrIpAddrMask(){
        return WifiHelper.getIpAddrMaskForInterfaces("eth0");
    }

    /*
     * 忽略当前的连接热点
     */
    public void ignoreConnectedWifi(){
        synchronized (mConnectLocker) {
            checkInit();
            if(mIsConnecting){
                L.w("Do nothing because wifi is connecting");
                return;
            }

            WifiInfo wifiInfo = WifiHelper.getConnectionInfo(mWifiManager);
            WifiHelper.ignoreWifi(mWifiManager, WifiHelper.removeDoubleQuotes(wifiInfo.getSSID()));

        }
    }

    /*
     * 取消保存的wifi
     */
    public void cancelWifi(String ssid){
        synchronized (mConnectLocker) {
            checkInit();
            if(mIsConnecting){
                L.w("Do nothing because wifi is connecting");
                return;
            }
            WifiHelper.removeExistWiFiConfiguration(mWifiManager, WifiHelper.removeDoubleQuotes(ssid));
        }
    }

    public enum SSID_STATE{
        SSID_STATE_NO_EXIST, SSID_STATE_EXIST_BUT_DISCONNECTED, SSID_STATE_EXIST_AND_CONNECTED
    }

    /*
     * 查询热点是否系统有保存和连接,
     * SSID_STATE_NO_EXIST 没有保存,
     * SSID_STATE_EXIST_BUT_DISCONNECTED 已保存但是连接不上,
     * SSID_STATE_EXIST_AND_CONNECTED 已保存,可以连接上
     */
    public SSID_STATE isSsidSaved(String ssid){
        checkInit();
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        WifiConfiguration findBean = null;
        for (WifiConfiguration existingConfig : existingConfigs) {
            android.util.Log.d("meng", "888888888888888 " + existingConfig.toString());
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                findBean =  existingConfig;
            }
        }

        if(null == findBean){
            return SSID_STATE.SSID_STATE_NO_EXIST;
        }

        if(!Utils.isOSVersionNougat()){
            try {
                boolean validate = (Boolean)ReflectUtils.getPropertyValue(findBean, "validatedInternetAccess");
                if(validate){
                    return SSID_STATE.SSID_STATE_EXIST_AND_CONNECTED;
                }else{
                    return SSID_STATE.SSID_STATE_EXIST_BUT_DISCONNECTED;
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
                return SSID_STATE.SSID_STATE_NO_EXIST;
            }
        }else {
            if (findBean.status == 1) {
                return SSID_STATE.SSID_STATE_EXIST_BUT_DISCONNECTED;
            } else {
                return SSID_STATE.SSID_STATE_EXIST_AND_CONNECTED;
            }
        }
    }

    private class WiFiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int switchState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);//得到WiFi开关状态值
                switch (switchState) {
                    case WifiManager.WIFI_STATE_DISABLED://WiFi已关闭
                        L.d("Wifi state receiver disabled ... ");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING://WiFi关闭中
                        L.d("Wifi state receiver disabling ... ");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED://WiFi已开启
                        L.d("Wifi state receiver enabled ... ");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING://WiFi开启中
                        L.d("Wifi state receiver enabling ... ");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN://WiFi状态未知
                        L.d("Wifi state receiver unknown ... ");
                        break;
                    default:
                        break;
                }
            }else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){ //网络状态改变行为
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);//得到信息包
                if (parcelableExtra != null){
                    NetworkInfo networkInfo = (NetworkInfo)parcelableExtra;//得到网络信息
                    NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
                    L.d("Network state receiver " + networkInfo.toString() + " thread = " + Utils.isOnMainThread());
                    CONNECT_STATE lastConnectState = mConnectState;
                    String lastConnectingSSID =  mConnectingSSID;
                    switch (detailedState){
                        case CONNECTED:
                            L.d("Network state receiver connected ... ");
                            mConnectState = CONNECT_STATE.CONNECTED;
                            mConnectingSSID = null;
                            synchronized (mConnectLocker) {
                                if(mIsConnecting) {
                                    if(!Utils.isOSVersionNougat()){
                                        ActWifi info = getConnectedWifi();
                                        if(info == null){
                                            return;
                                        }
                                        String ssid = info.SSID;
                                        if (ssid != null && ssid.equals(mConnectSSID)) {
                                            mIsConnecting = false;
                                            if (null != mConnectCallback) {
                                                mConnectCallback.onSucess();
                                                mConnectCallback = null;
                                            }
                                        }
                                    }else {
                                        WifiInfo info = mWifiManager.getConnectionInfo();
                                        String ssid = WifiHelper.removeDoubleQuotes(info.getSSID());
                                        if (ssid != null && ssid.equals(mConnectSSID)) {
                                            mIsConnecting = false;
                                            if (null != mConnectCallback) {
                                                mConnectCallback.onSucess();
                                                mConnectCallback = null;
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case DISCONNECTED:
                            mConnectState = CONNECT_STATE.DISCONNECT;
                            mConnectingSSID = null;
                            L.d("Network state receiver disconnected ... ");
                            break;
                        case CONNECTING:
                            mConnectState = CONNECT_STATE.CONNECTING;;
                            mConnectingSSID = WifiHelper.removeDoubleQuotes(networkInfo.getExtraInfo());
                            L.d("Network state receiver connecting ... " + mConnectingSSID);
                            break;
                        case DISCONNECTING:
                            L.d("Network state receiver disconnecting ... ");
                            break;
                        case FAILED:
                            mConnectState = CONNECT_STATE.DISCONNECT;;
                            mConnectingSSID = null;
                            L.d("Network state receiver failed ... ");
                            break;
                        default:
                            break;
                    }

                    if(null != mConnectStateCallback && (lastConnectState != mConnectState
                        || !Utils.isStringEquals(lastConnectingSSID, mConnectingSSID))){
                        L.d("Network state receiver need notify UI ");
                        mConnectStateCallback.onChange();
                    }
                }
            }else if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                L.d("Network supplicant error = " + linkWifiResult
                        + " " + (linkWifiResult == WifiManager.ERROR_AUTHENTICATING ? "password error" : "")
                        + " thread = " + Utils.isOnMainThread());
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    synchronized (mConnectLocker) {
                        if(mIsConnecting) {
                            mIsConnecting = false;
                            if (null != mConnectCallback) {
                                mConnectCallback.onFail("密码错误");
                                mConnectCallback = null;
                            }
                        }
                    }
                }

                //just for test
                SupplicantState supplicantState = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                L.d("Network supplicant new state = " + supplicantState);
                if (supplicantState == (SupplicantState.COMPLETED)){
                    L.i("SUPPLICANTSTATE ---> Connected");
                    //do something
                }

                if (supplicantState == (SupplicantState.DISCONNECTED)){
                    L.i("SUPPLICANTSTATE ---> Disconnected");
                    //do something
                }
            }else if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                L.d("Network scan available ... " + Utils.isOnMainThread());
                doScanResult();
            }
        }
    }
}
