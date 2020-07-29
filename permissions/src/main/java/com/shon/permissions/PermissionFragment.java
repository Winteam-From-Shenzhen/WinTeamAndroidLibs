package com.shon.permissions;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Auth : xiao.yunfei
 * Date : 2020/6/29 10:06
 * Package name : org.eson.permissions
 * Des :
 */
public class PermissionFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_CODE = 88;
    private OnPermissionCallback permissionCallback;

    private String TAG = PermissionFragment.class.getSimpleName();

    PermissionFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions) {

        //检查权限，
        // 避免重复申请，加载 fragment，
        // 造成 原来 activity或者 fragment 的生命周期 的重复调用
        PermissionCheck.hasPerMissions(getActivity(), permissions,
                onPermissionCallback);
    }

    /**
     *
     */
    private OnPermissionCallback onPermissionCallback = new OnPermissionCallback() {
        @Override
        public void onRequest(@NonNull boolean granted, @Nullable String[] reRequest) {
            if (granted || reRequest == null) {
                return;
            }
            requestPermissions(reRequest, PERMISSIONS_REQUEST_CODE);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }
        //有些权限申请，需要具体的说明信息，此处保存需要显示详细说明的权限
        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    /**
     * 申请结果处理
     *
     * @param permissions                          permissions
     * @param grantResults                         grantResults
     * @param shouldShowRequestPermissionRationale shouldShowRequestPermissionRationale //需要详细说明的
     */
    private void onRequestPermissionsResult(String[] permissions, int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        ArrayList<Boolean> grantedArray = new ArrayList<>();
        for (int i = 0, size = permissions.length; i < size; i++) {
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            Log.e(TAG, "granted = " + granted);
            if (granted) {
                grantedArray.add(granted);
            }
        }
        if (permissionCallback == null) {
            return;
        }
        if (grantedArray.size() == grantResults.length) {
            permissionCallback.onRequest(true, null);
        } else {
            permissionCallback.onRequest(false, null);
        }
    }

    public void setOnRequestCallBack(@NonNull OnPermissionCallback onPermissionCallback) {
        this.permissionCallback = onPermissionCallback;
    }
}
