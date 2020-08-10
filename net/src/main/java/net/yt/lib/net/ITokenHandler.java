package net.yt.lib.net;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/30 16:37
 * Package name : net.yt.whale.net
 * Des :
 */
public interface ITokenHandler {

    //覆写获取token的方法
    String getToken();

    /**
     * 2020/8/10 新增
     *
     * 获取 token key
     *
     * @return token key
     */
    String getTokenKey();

    void onTokenError();
}
