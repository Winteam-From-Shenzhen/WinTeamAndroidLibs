package net.yt.lib.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import net.yt.lib.log.L;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

class WifiHelper {
    public static String removeDoubleQuotes(String string) {
        if (string == null){
            return null;
        }
        final int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    public static String addQuotationMarks(String content) {
        if (content == null) {
            return null;
        }
        final int length = content.length();
        if ((length > 1) && (content.charAt(0) == '"') && (content.charAt(length - 1) == '"')) {
            return content;
        }
        content = "\"" + content + "\"";
        return content;
    }

    /**
     * 将idAddress转化成string类型的Id字符串
     *
     * @param idString
     * @return
     */
    private static String getStringId(int idString) {
        StringBuffer sb = new StringBuffer();
        int b = (idString >> 0) & 0xff;
        sb.append(b + ".");
        b = (idString >> 8) & 0xff;
        sb.append(b + ".");
        b = (idString >> 16) & 0xff;
        sb.append(b + ".");
        b = (idString >> 24) & 0xff;
        sb.append(b);
        return sb.toString();
    }

    public static boolean is24GHz(int freq) {
        return freq > 2400 && freq < 2500;
    }

    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    public static int securityType(String capbility) {
        if (capbility == null || capbility.isEmpty()) {
            return Constant.SECURITY_NONE;
        }
        // 如果包含WAP-PSK的话，则为WAP加密方式
        if (capbility.contains("WPA-PSK") || capbility.contains("WPA2-PSK")) {
            return Constant.SECURITY_PSK;
        } else if (capbility.contains("WPA2-EAP")) {
            return Constant.SECURITY_EAP;
        } else if (capbility.contains("WEP")) {
            return Constant.SECURITY_WEP;
        } else if (capbility.contains("ESS")) {
            // 如果是ESS则没有密码
            return Constant.SECURITY_NONE;
        }
        return Constant.SECURITY_NONE;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    /**
     * WifiInfo.MIN_RSSI = -126;
     * WifiInfo.MAX_RSSI = 200;
     *
     * Quality     Excellent           Good            Fair            Poor
     * dBm        -30 ～ -61        -63 ～ -73     -75 ～ -85        -87 ～ -97
     *
     * @param rssi
     * @return
     */
    public static int formatRssi(int rssi) {
        if (rssi < -97){
            return 0;
        }else if (rssi < -87){
            return 1;
        }else if (rssi < -75){
            return 2;
        }else if (rssi < -63){
            return 3;
        }else {
            return 4;
        }
    }

    //ip地址转换
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /*
     * 获取正在连接中的WiFiInfo,这个数据是永远不会是空的,如果一次WiFi都没连接过那么它携带的数据会有空的,
     * 如果连接过然后又断开了,那么你在这里获取的就是之前连接的WiFi.
     */
    public static WifiInfo getConnectionInfo(WifiManager wm) {
        WifiInfo wifiInfo = wm.getConnectionInfo();
        wifiInfo.getSSID();//WiFi名称
        wifiInfo.getRssi();//信号强度
        wifiInfo.getIpAddress();//ip地址
        wifiInfo.getFrequency();//频率 比如2.4G（boolean is24G = frequency > 2400 && frequency < 2500;） 或者 5G （boolean is5G = frequency > 4900 && frequency < 5900;）的WiFi
        wifiInfo.getNetworkId();//id
        wifiInfo.getLinkSpeed();//网络链接速度
        wifiInfo.getSupplicantState();//获取请求状态
        return wifiInfo;
    }

    // 查看以前是否也配置过这个网络
    public static WifiConfiguration isExsits(WifiManager wm, String SSID) {
        List<WifiConfiguration> existingConfigs = wm.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 创建一个wifi连接信息
     *
     * @param ssid ssid
     * @param pass password
     * @param type 连接类型
     * @return wifi连接信息
     */
    public static WifiConfiguration createWifiConfiguration(WifiManager wm, String ssid, String pass, int type) {
        WifiConfiguration configuration = new WifiConfiguration();
        //清除一些默认wifi的配置
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + ssid + "\"";
        //清除已存在的相应的ssid
        removeExistWiFiConfiguration(wm, ssid);

        // 分为三种情况：1没有密码2用wep加密3用wpa加密
        switch (type) {
            case Constant.SECURITY_NONE:       //没有密码
                //configuration.wepKeys[0] = "";
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //configuration.wepTxKeyIndex = 0;
                break;
            case Constant.SECURITY_WEP:      //wep密码
                if (!TextUtils.isEmpty(pass)) {
                    if (isHexWepKey(pass)) {
                        configuration.wepKeys[0] = pass;
                    } else {
                        configuration.wepKeys[0] = "\"" + pass + "\"";
                    }
                }
                //configuration.hiddenSSID = true;
                //configuration.wepKeys[0] = "\"" + pass + "\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                //configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                //configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                //configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                //configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.wepTxKeyIndex = 0;
                break;
            case Constant.SECURITY_PSK:   //wpa or wpa2 密码
                configuration.preSharedKey = "\"" + pass + "\"";
                //configuration.hiddenSSID = true;
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                configuration.status = WifiConfiguration.Status.ENABLED;
                break;

            case Constant.SECURITY_EAP: //暂不支持
                /*
                WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
                configuration.SSID = ssid;
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                enterpriseConfig.setIdentity(ssid);
                enterpriseConfig.setPassword(pass);
                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
                configuration.enterpriseConfig = enterpriseConfig;
                */
                return null;

        }
        return configuration;
    }

    /**
     * 移除已保存的连接列表中存在的ssid
     *
     * @param ssid ssid
     */
    public static void removeExistWiFiConfiguration(WifiManager wm, String ssid) {
        List<WifiConfiguration> wificonfigList = wm.getConfiguredNetworks();
        if (wificonfigList == null) return;
        if (wificonfigList.size() < 1) return;
        for (WifiConfiguration wifi : wificonfigList) {
            if (wifi.SSID.equals("\"" + ssid + "\"")) {
                wm.removeNetwork(wifi.networkId);
                wm.saveConfiguration();
            }
        }
    }

    /**
     * 移除所有保存在列表的wifi信息
     */
    public static void removeExistWifiConfiguration(WifiManager wm) {
        List<WifiConfiguration> wificonfigList = wm.getConfiguredNetworks();
        if (wificonfigList == null) return;
        if (wificonfigList.size() < 1) return;
        for (WifiConfiguration wifi : wificonfigList) {
            wm.removeNetwork(wifi.networkId);
            wm.saveConfiguration();
        }
    }

    /*
     * 忽略当前连接的网络
     */
    public static void ignoreWifi(WifiManager wm, String ssid) {
        ssid = addQuotationMarks(ssid);
        int netWorkId = -1;
        List<WifiConfiguration> wifiConfigs = wm.getConfiguredNetworks();
        if (wifiConfigs == null || wifiConfigs.size() == 0) {
            L.w("ignoreWifi is not in configs " + ssid);
            return;
        }
        for (WifiConfiguration wifiConfig : wifiConfigs) {
            if (TextUtils.equals(wifiConfig.SSID, ssid)) {
                netWorkId = wifiConfig.networkId;
                break;
            }
        }
        if(-1 != netWorkId){
            wm.disconnect();
            wm.removeNetwork(netWorkId);
            wm.disableNetwork(netWorkId);
            wm.saveConfiguration();
            wm.reconnect();
        }else{
            L.w("ignoreWifi ssid id is -1 " + ssid);
        }
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = null;
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {

        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress =  null;
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            L.dd("WifiHelper", "all:" + all.size());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                L.dd("WifiHelper", "macBytes:" + macBytes.length + "," + nif.getName());

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取设备mac地址
    public static String getMacFromHardware(Context context) {

        String macAddress = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){//5.0以下
            macAddress = getMacDefault(context);
            if (macAddress != null ) {
                L.dd("WifiHelper", "android 5.0以前的方式获取mac"+macAddress);
                String macAddressTmp =  macAddress.replaceAll(":","");
                if(macAddressTmp.equalsIgnoreCase("020000000000")== false){
                    return macAddress;
                }
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            macAddress = getMacAddress();
            if (macAddress != null ) {
                L.dd("WifiHelper", "android 6~7 的方式获取的mac"+macAddress);
                String macAddressTmp =  macAddress.replaceAll(":","");
                if(macAddressTmp.equalsIgnoreCase("020000000000")== false){
                    return macAddress;
                }
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            macAddress = getMacFromHardware();
            if (macAddress != null ) {
                L.dd("WifiHelper", "android 7以后 的方式获取的mac"+macAddress);
                String macAddressTmp =  macAddress.replaceAll(":","");
                if(macAddressTmp.equalsIgnoreCase("020000000000") == false){
                    return macAddress;
                }
            }
        }

        L.dd("WifiHelper", "没有获取到MAC");
        return null;
    }

    /**
     * 子网掩码
     */
    public static String getIpAddrMaskForInterfaces(String interfaceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();    //获取本机所有的网络接口
            while (networkInterfaceEnumeration.hasMoreElements()) { //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement(); //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) { //判断网口是否在使用，判断是否时我们获取的网口
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {    //
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {    //仅仅处理ipv4
                        return calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength());   //获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "error";
    }

    //通过子网掩码的位数计算子网掩码
    private static String calcMaskByPrefixLength(int length) {
        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

}
