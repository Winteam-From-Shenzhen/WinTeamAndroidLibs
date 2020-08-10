package net.yt.whale.net.callback;

import net.yt.whale.net.BaseResult;
import net.yt.whale.net.util.NetExecutors;
import net.yt.whale.net.util.RetrofitLog;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/21 20:23
 * Package name : net.yt.whale.net.callback
 * Des : 基本的请求 callback
 */
public abstract class CommonCallback<T> implements Callback<T> {

    @SuppressWarnings("unChecked")
    @Override
    public void onResponse(@NotNull Call<T> call, final @NotNull Response<T> response) {
        try {
            RetrofitLog.d("onResponse : " + response.toString());

            if (response.code() == 200) {
                final T result = response.body();
                if (result == null) {
                    RetrofitLog.e("CommonCallback : response.body is null");
                    runOnMain(() -> onError(response.code(), response.message()));
                    return;
                }
                RetrofitLog.d("onResponse : result = " + response.body());

                if (response.body() instanceof BaseResult) {

                    BaseResult baseResult = (BaseResult) response.body();
                    RetrofitLog.d("onResponse : result = " + baseResult.toString());
                    if (baseResult.getCode() == 0 || baseResult.getCode() == 200){
                        runOnMain(() -> onSuccess(result));
                    }else {
                        runOnMain(() -> onError(baseResult.getCode(), baseResult.getMsg()));
                    }
                } else {
                    runOnMain(() -> onSuccess(result));
                }

            } else {
                ExceptionHandle.ResponeThrowable responeThrowable = ExceptionHandle.handleException(response.code());
                runOnMain(() -> onError(responeThrowable.code, responeThrowable.message));
            }

        } catch (Exception e) {
            runOnMain(() -> onError(-1, "服务器返回数据异常"));
        }

    }

    @Override
    public void onFailure(@NotNull Call<T> call, @NotNull final Throwable t) {
        RetrofitLog.e("onFailure : " + t.toString());
        ExceptionHandle.ResponeThrowable responeThrowable = ExceptionHandle.handleException(t);
        runOnMain(() -> onError(responeThrowable.code, responeThrowable.message));
    }

    private void runOnMain(Runnable runnable) {
        NetExecutors.getInstance().mainThread().execute(runnable);
    }

    public abstract void onSuccess(T t);

    public abstract void onError(int errorCode, String msg);
}
