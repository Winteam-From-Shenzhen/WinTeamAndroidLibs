package net.yt.lib.wifi;

public class WifiBean {
    public enum State{
        NONE, CONNECT, CONNECTING, SAVE_ENABLE, SAVE_DISABLE
    }

    public String SSID; //WiFi的名称
    public String BSSID; //WiFi的接入点mac
    public long refreshTime; //刷新时间
    public int level; //信号等级
    public int frequency; //频率
    public int security; //加密方式

    public State state; //列表状态

    public WifiBean copy(){
        WifiBean newBean = new WifiBean();
        newBean.SSID = this.SSID;
        newBean.BSSID = this.BSSID;
        newBean.refreshTime = this.refreshTime;
        newBean.level = this.level;
        newBean.frequency = this.frequency;
        newBean.security = this.security;
        newBean.state = this.state;
        return newBean;
    }

    public String toString(){
        return "WifiBean{ SSID=" + SSID
                + ", " + "BSSID=" + BSSID
                + ", " + "refreshTime=" + refreshTime
                + ", " + "level=" + level
                + ", " + "frequency=" + frequency
                + ", " + "security=" + security
                + " }";
    }
}
