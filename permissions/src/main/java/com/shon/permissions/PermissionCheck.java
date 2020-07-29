package com.shon.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Auth : xiao_yun_fei
 * Date : 2020/7/26 11:27
 * Package name : com.shon.permissions
 * Des : 权限检查 工具
 */
public class PermissionCheck {

    private static String TAG = PermissionCheck.class.getSimpleName();

    /**
     * 对单一权限检查
     *
     * @param context    cxt
     * @param permission 需要检查的权限
     * @return 是否具有权限
     */
    public static boolean hasPermission(Context context, String permission) {
        if (context == null || TextUtils.isEmpty(permission)) {
            //如果为空，默认具有此权限
            return true;
        }
        int result = ContextCompat.checkSelfPermission(context, permission);
        boolean has = (PackageManager.PERMISSION_GRANTED == result);
        Log.d(TAG, "permission :" + permission + " ; result = " + has);
        return has;
    }

    /**
     * 对多个权限进行检查
     *
     * @param context              cxt
     * @param permissions          多个权限
     * @param onPermissionCallback 结果回调 ，不能为空
     */
    public static void hasPerMissions(Context context, String[] permissions,
                                      @NonNull OnPermissionCallback onPermissionCallback) {

        if (context == null || permissions == null || permissions.length == 0) {
            //默认有权限
            onPermissionCallback.onRequest(true, null);
            return;
        }

        // 缓存没有获取的权限
        List<String> needRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (TextUtils.isEmpty(permission)) {
                continue;
            }
            if (hasPermission(context, permission)) {
                continue;
            }
            needRequest.add(permission);
        }
        if (needRequest.size() == 0) {
            //全部允许
            onPermissionCallback.onRequest(true, null);
        } else {
            //不是全部允许，回传没有允许的权限
            String[] reRequestPermissions = new String[needRequest.size()];
            onPermissionCallback.onRequest(false, needRequest.toArray(reRequestPermissions));
        }
    }
}
