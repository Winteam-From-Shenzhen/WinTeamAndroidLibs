package net.yt.lib.net.callback;

import net.yt.lib.log.L;
import net.yt.lib.net.BaseResult;
import net.yt.lib.net.util.NetExecutors;

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
            L.d("onResponse : " + response.toString());

            if (response.code() == 200) {
                final T result = response.body();
                if (result == null) {
                    L.e("CommonCallback : response.body is null");
                    runOnMain(() -> onError(response.code(), response.message()));
                    return;
                }
                L.d("onResponse : result = " + response.body());

                if (response.body() instanceof BaseResult) {
                    int code = ((BaseResult)result).getCode();
                    if ( code == 0 || code == 200){
                        runOnMain(() -> onSuccess(result));
                    }else {
                        runOnMain(() -> onError(code, ((BaseResult)result).getMessage()));
                    }
                } else {
                    runOnMain(() -> onSuccess(result));
                }

            } else {
//                ExceptionHandle.ResponeThrowable responeThrowable = ExceptionHandle.handleException(response.code());
                runOnMain(() -> onError(response.code(), response.message()));
            }

        } catch (Exception e) {
            runOnMain(() -> onError(-1, "服务器返回数据异常"));
        }

    }

    @Override
    public void onFailure(@NotNull Call<T> call, @NotNull final Throwable t) {
        L.e("onFailure : " + t.toString());
        ExceptionHandle.ResponeThrowable responeThrowable = ExceptionHandle.handleException(t);
        runOnMain(() -> onError(responeThrowable.code, responeThrowable.message));
    }

    private void runOnMain(Runnable runnable) {
        NetExecutors.getInstance().mainThread().execute(runnable);
    }

    public abstract void onSuccess(T t);

    public abstract void onError(int errorCode, String msg);
}
