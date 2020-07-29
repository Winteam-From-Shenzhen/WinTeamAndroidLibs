package com.shon.permissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Auth : xiao.yunfei
 * Date : 2020/6/29 10:42
 * Package name : org.eson.permissions
 * Des :
 */
public class PermissionRequest {


    private static final String TAG = PermissionRequest.class.getName();
    private PermissionFragment rxPermissionsFragment;
    private WeakReference<AppCompatActivity> weakReference;
    private ArrayList<String> permissionList;

    private OnPermissionCallback onRequestPermissionCallback;


    private PermissionRequest(AppCompatActivity activity) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }

        weakReference = new WeakReference<>(activity);
        FragmentManager fragmentManager = weakReference.get().getSupportFragmentManager();
        createPermissionFragment(fragmentManager);
    }

    private PermissionRequest(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        createPermissionFragment(fragmentManager);
    }

    public void requestPermissions() {
        if (onRequestPermissionCallback == null){
            return;
        }
        if (permissionList.size() ==0){
            onRequestPermissionCallback.onRequest(true,null);
            return;
        }

        String[] perm = new String[permissionList.size()];
        if (rxPermissionsFragment != null) {
            rxPermissionsFragment.setOnRequestCallBack(onRequestPermissionCallback);
            rxPermissionsFragment.requestPermissions((String[]) permissionList.toArray(perm));
        }
    }

    private void addPermission(String permission) {
        checkList();
        addPermissionToList(permission);
    }

    private void addPermissions(String... permissions) {
        checkList();
        for (String permission : permissions) {
            addPermissionToList(permission);
        }
    }

    private void checkList() {
        if (permissionList == null) {
            permissionList = new ArrayList<>();
        }
    }

    private void addPermissionToList(String permission) {
        if (permissionList.contains(permission)) {
            return;
        }
        permissionList.add(permission);
    }

    private void createPermissionFragment(@NonNull final FragmentManager fragmentManager) {
        synchronized (PermissionRequest.class) {
            if (rxPermissionsFragment == null) {
                rxPermissionsFragment = getPermissionsFragment(fragmentManager);
            }
        }
    }

    private PermissionFragment getPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        PermissionFragment rxPermissionsFragment = findRxPermissionsFragment(fragmentManager);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new PermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitNow();
        }
        return rxPermissionsFragment;
    }

    private PermissionFragment findRxPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        return (PermissionFragment) fragmentManager.findFragmentByTag(TAG);
    }

    private void setOnRequestCallBack(OnPermissionCallback permissionCallback) {
        this.onRequestPermissionCallback = permissionCallback;
    }


    /**
     * PermissionRequest.Builder
     */
    public static class Builder {
        private PermissionRequest permissionRequest;

        public Builder(AppCompatActivity appCompatActivity) {
            permissionRequest = new PermissionRequest(appCompatActivity);
        }

        public Builder(Fragment fragment) {
            permissionRequest = new PermissionRequest(fragment);
        }

        public Builder addPermissions(String... permissions) {

            permissionRequest.addPermissions(permissions);
            return this;
        }

        public Builder addPermission(String permission) {
            permissionRequest.addPermission(permission);
            return this;
        }

        public Builder setCallback(OnPermissionCallback onRequestPermissionCallback) {
            permissionRequest.setOnRequestCallBack(onRequestPermissionCallback);
            return this;
        }

        public PermissionRequest build() {
            return permissionRequest;
        }
    }

}
