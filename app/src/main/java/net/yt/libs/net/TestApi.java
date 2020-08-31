package net.yt.libs.net;

import net.yt.lib.net.BaseApi;
/**
 * Auth : xiao.yunfei
 * Date : 2020/8/31 19:38
 * Package name : net.yt.libs.net
 * Des :
 */
public class TestApi extends BaseApi<ApiImp> {
    public TestApi() {
        super("");


    }

//    public ApiImp getApiImp(){
//        OkHttpClientBuild.Builder builder = new OkHttpClientBuild.Builder();
//        OkHttpClient.Builder okHttpClientBuilder = builder.enableHttps(enableHttps).needLog(needLog).setInterceptor(interceptors).builder();
//
//        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
//                .client(okHttpClientBuilder.build())
//                .baseUrl("")
//                .addConverterFactory(GsonConverterFactory.create());
//
//
//        return retrofitBuilder.build().create(ApiImp.class);
//    }
}