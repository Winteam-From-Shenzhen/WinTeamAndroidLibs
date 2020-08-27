package net.yt.lib.net;

import net.yt.lib.log.L;
import net.yt.lib.net.interceptor.TokenAddInterceptor;
import net.yt.lib.net.interceptor.TokenErrorInterceptor;
import net.yt.lib.net.ssl.TrustAllCerts;
import net.yt.lib.net.ssl.TrustAllHostnameVerifier;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/21 12:01
 * Package name : net.yt.whale.net
 * Des : 创建 OkHttpClient
 */
class OkHttpClientBuild {


    private static TokenAddInterceptor tokenAddInterceptor;

    /**
     * 获取 TokenAddInterceptor
     *
     * @param token token
     * @return TokenAddInterceptor
     */
    private static TokenAddInterceptor getTokenInterceptor(String key, String token) {
        if (tokenAddInterceptor == null) {
            tokenAddInterceptor = new TokenAddInterceptor(token);
        }
        tokenAddInterceptor.updateToken(key, token);
        return tokenAddInterceptor;
    }

    /**
     * 更新 TokenAddInterceptor 的中的 token
     *
     * @param token token
     */
    public static void updateTokenAddInterceptor(String key, String token) {
        getTokenInterceptor(key, token);
    }


    /**
     * 创建  OkHttpClient ，根据token 是否为空，决定是否添加 token 拦截
     *
     * @param token token
     * @return OkHttpClient
     */
    public static OkHttpClient build(String token, ITokenHandler tokenHandler) {
        OkHttpClient.Builder builder = getDefaultBuild();
        builder.addInterceptor(getTokenInterceptor("", token));         //设置 Token拦截器, 添加 token 使用
        builder.addInterceptor(new TokenErrorInterceptor(tokenHandler)); //设置 返回 Token失效 拦截器
        return builder.build();
    }


    /**
     * 基本网络请求。 添加 HTTPS 认证。
     * 日志打印
     *
     * @return OkHttpClient.Builder
     */
    private static OkHttpClient.Builder getDefaultBuild() {
        OkHttpClient.Builder builder = getBaseBuild();

        builder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts());
        builder.hostnameVerifier(new TrustAllHostnameVerifier());

        if (Config.isIsDebug()) {                 //设置网络日志拦截器
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> L.i("网络日志: " + message));
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        return builder;
    }

    /**
     * 基础 OkHttpClient.Builder , 只设置超时
     *
     * @return OkHttpClient.Builder
     */
    public static OkHttpClient.Builder getBaseBuild() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);             //失败是否重连
        builder.connectTimeout(Config.getConnectTime(), TimeUnit.SECONDS); //连接超时
        builder.readTimeout(Config.getReadTime(), TimeUnit.SECONDS);       //读取超时
        builder.writeTimeout(Config.getWriteTime(), TimeUnit.SECONDS);     //写入超时

        return builder;
    }

    /**
     * SSLSocketFactory
     *
     * @return SSLSocketFactory
     */
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{
                    new TrustAllCerts()
            }, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    public static class Builder {
        private OkHttpClient.Builder builder;

        Builder() {
            builder = getBaseBuild();
        }

        Builder needLog(boolean needLog) {
            if (needLog) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> L.i("网络日志: " + message));
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(httpLoggingInterceptor);
            }
            return this;
        }

        Builder enableHttps(boolean enable) {
            if (enable) {
                builder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts());
                builder.hostnameVerifier(new TrustAllHostnameVerifier());
            }
            return this;
        }

        Builder setInterceptor(Interceptor... interceptors) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
            return this;
        }

        OkHttpClient.Builder builder() {
            return builder;
        }
    }
}
