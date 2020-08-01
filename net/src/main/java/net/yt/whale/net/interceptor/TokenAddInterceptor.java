package net.yt.whale.net.interceptor;


import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/21 12:02
 * Package name : net.yt.whale.net.interceptor
 * Des : 给 请求添加 token 的拦截器
 */
public class TokenAddInterceptor implements Interceptor {

    private String mToken;

    public TokenAddInterceptor(String token) {
        mToken = token;
    }

    public void updateToken(String newToken) {
        mToken = newToken;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        String token = mToken;
        String tokenKey = "token";

        builder.addHeader(tokenKey, TextUtils.isEmpty(token) ? "" : token); //增加token
        Request request = builder.build();

        return chain.proceed(request);
    }
}
