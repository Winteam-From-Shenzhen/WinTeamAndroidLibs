package net.yt.libs.test;

import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import net.yt.lib.sdk.base.BaseActivity;
import net.yt.lib.sdk.utils.ToastUtils;
import net.yt.libs.permissions.OnPermissionCallback;
import net.yt.libs.permissions.PermissionCheck;
import net.yt.libs.permissions.PermissionRequest;

public class MainActivity extends BaseActivity {

    private static int sCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        $(R.id.push_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(v.getId() == R.id.push_btn){
//                    Intent intent = new Intent(MainActivity.this, PushActivity.class);
//                    startActivity(intent);

//                    ToastUtils.showLongToast("点击了 " + sCount++ + " 次");

                    //Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                    //startActivity(wifiSettingsIntent);

                    Intent i = new Intent();
                    i.setClass(MainActivity.this, net.yt.libs.test.wifi.WifiSettingActivity.class);
                    startActivity(i);
                }
            }
        });

        //testPermissions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://增加点击事件
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void testPermissions() {
        boolean canReadStorage = PermissionCheck.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (canReadStorage) {
            return;
        }


//        PermissionCheck.hasPerMissions(this, new String[]{}, new OnPermissionCallback() {
//            @Override
//            public void onRequest(boolean granted, @Nullable String[] reRequest) {
//
//
//            }
//        });
        PermissionRequest permissionRequest = new PermissionRequest.Builder(this)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setCallback(new OnPermissionCallback() {
                    @Override
                    public void onRequest(boolean granted, @Nullable String[] reRequest) {

                    }
                })
                .build();

        permissionRequest.requestPermissions();
    }
}