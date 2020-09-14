package net.yt.lib.wifi;

public class Constant {
    public static final int SECURITY_NONE = 0; 	//开放网络，不加密，无需密码
    public static final int SECURITY_WEP = 1;  //旧的加密方式，不推荐使用，仅需密码
    public static final int SECURITY_PSK = 2;  //最常见的加密方式，仅需密码
    public static final int SECURITY_EAP = 3;  //企业加密方式，ID+密码验证
}
