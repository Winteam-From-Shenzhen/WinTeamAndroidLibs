package net.yt.libs.permissions;

import androidx.annotation.Nullable;

/**
 * Auth : xiao.yunfei
 * Date : 2020/6/29 17:41
 * Package name : org.eson.permissions
 * Des : 权限请求 & 检查结果 callback
 */
public interface OnPermissionCallback {
    /**
     * 结果
     *
     * @param granted   请求多个权限时，如果有一个没有被允许则为 false，全部已允许则为true
     * @param reRequest 没有被允许的权限，使用者可以选择重新申请，或者其他
     */
    void onRequest(boolean granted, @Nullable String[] reRequest);
}
