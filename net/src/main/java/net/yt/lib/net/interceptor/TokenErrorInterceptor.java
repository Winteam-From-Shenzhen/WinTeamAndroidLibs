package net.yt.lib.net.interceptor;

import net.yt.lib.log.L;
import net.yt.lib.net.ITokenHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Auth : xiao.yunfei
 * Date : 2020/8/1 14:16
 * Package name : net.yt.whale.net.interceptor
 * Des : 返回 token 失效的拦截器，
 * 目的是 能够方便统一处理失效的状态
 */
public class TokenErrorInterceptor implements Interceptor {
    private ITokenHandler iTokenHandler;

    public TokenErrorInterceptor(ITokenHandler tokenHandler) {
        this.iTokenHandler = tokenHandler;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int code = response.code();
        if (isTokenExpired(code)) {//根据和服务端的约定判断token过期
            L.e("拦截到 token  已过期");
            if (iTokenHandler != null) {
                iTokenHandler.onTokenError();
            }
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response response
     * @return 是否失效
     */
    private boolean isTokenExpired(int response) {
        return response == 401;
    }

}