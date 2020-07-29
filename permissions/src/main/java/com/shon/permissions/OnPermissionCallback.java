package com.shon.permissions;

import androidx.annotation.Nullable;

/**
 * Auth : xiao.yunfei
 * Date : 2020/6/29 17:41
 * Package name : org.eson.permissions
 * Des :
 */
public interface OnPermissionCallback {
    void onRequest(boolean granted,@Nullable String[] reRequest);
}
