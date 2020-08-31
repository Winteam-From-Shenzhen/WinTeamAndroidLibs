package net.yt.lib.net;

import android.text.TextUtils;

import net.yt.lib.log.L;
import net.yt.lib.net.interceptor.TokenAddInterceptor;
import net.yt.lib.net.interceptor.TokenErrorInterceptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/21 11:19
 * Package name : net.yt.whale.net
 * Des : BaseApi
 * <p>
 * 本类是所有 API  的基类，只做两件事
 * <p>
 * 1、 创建 Retrofit的实例
 * 2、创建 ApiInterface(即 使用者自己写的 网络接口类) 的实例
 */
public abstract class BaseApi<ApiImp> implements ITokenHandler {

    protected Retrofit mRetrofit;
    private ApiImp apiInterface;
    private String cacheToken = "";
    private Retrofit.Builder retrofitBuilder;

    private String baseUrl;
    private TokenAddInterceptor tokenAddInterceptor;

    public BaseApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 自定义 retrofitBuilder
     *
     * @param enableHttps  是否启用 Https
     * @param needLog      是否开启 日志
     * @param interceptors 拦截器
     * @return ApiImp
     */
    public synchronized ApiImp getApiInterface(boolean enableHttps, boolean needLog, Interceptor... interceptors) {

        OkHttpClientBuild.Builder builder = new OkHttpClientBuild.Builder();
        OkHttpClient.Builder okHttpClientBuilder = builder.enableHttps(enableHttps).needLog(needLog).setInterceptor(interceptors).builder();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());

        Class<ApiImp> apiImpClass = getApiImp(this);
        if (apiImpClass == null) {
            throw new NullPointerException("apiImpClass is null");
        }
        return retrofitBuilder.build().create(apiImpClass);
    }

    /**
     * 获取  ApiImp 实例
     *
     * @return ApiImp
     */
    public synchronized ApiImp getApiInterface() {

        if (TextUtils.isEmpty(baseUrl) || !baseUrl.toLowerCase().startsWith("http")){
            throw new IllegalArgumentException("baseUrl is null  or  baseUrl is not http&https");
        }
        if (retrofitBuilder == null) {
            retrofitBuilder = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create());
        }
        String token = getToken();

        if (!TextUtils.equals(cacheToken, token)) {
            //token有更新
            L.d("用户 token 有更新");
            cacheToken = token;
            updateTokenAddInterceptor(getTokenKey(), cacheToken);
        }
        if (mRetrofit == null) {
            mRetrofit = retrofitBuilder.build();
        }
        if (apiInterface == null) {
            //更新 apiInterface
            Class<ApiImp> apiImpClass = getApiImp(this);
            if (apiImpClass == null) {
                throw new NullPointerException("apiImpClass is null");
            }
            apiInterface = mRetrofit.create(apiImpClass);
        }
        return apiInterface;
    }

    /**
     * 获取 OkHttpClient,
     * 使用者 如需要自己实现，直接重写此方法
     *
     * @return OkHttpClient
     */
    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder defaultBuild = OkHttpClientBuild.getDefaultBuild();
        defaultBuild.addInterceptor(getTokenInterceptor("", cacheToken));         //设置 Token拦截器, 添加 token 使用
        defaultBuild.addInterceptor(new TokenErrorInterceptor(this)); //设置 返回 Token失效 拦截器
        return defaultBuild.build();
    }
    /**
     * 获取 TokenAddInterceptor
     *
     * @param token token
     * @return TokenAddInterceptor
     */
    private TokenAddInterceptor getTokenInterceptor(String key, String token) {
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
    private void updateTokenAddInterceptor(String key, String token) {
        getTokenInterceptor(key, token);
    }
    /**
     * 返回 ApiImp.class
     *
     * @return ApiImp.class
     */
    @SuppressWarnings("unchecked")
    private Class<ApiImp> getApiImp(BaseApi<?> baseApi) {
        Type genType = baseApi.getClass().getGenericSuperclass();
        if (genType == null) {
            return null;
        }
        Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<ApiImp>) types[0];
    }

    @Override
    public String getToken() {
        return "";
    }

    @Override
    public String getTokenKey() {
        return "";
    }

    @Override
    public void onTokenError() {

    }

}
