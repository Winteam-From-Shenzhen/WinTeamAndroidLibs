package net.yt.whale.net;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/30 16:37
 * Package name : net.yt.whale.net
 * Des :
 */
public interface ITokenHandler {

    //覆写获取token的方法
    String getToken();

    void onTokenError();
}
