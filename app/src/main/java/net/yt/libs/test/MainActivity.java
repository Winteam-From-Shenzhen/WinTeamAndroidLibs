package net.yt.libs.test;

import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.yt.lib.sdk.base.BaseActivity;
import net.yt.libs.permissions.OnPermissionCallback;
import net.yt.libs.permissions.PermissionCheck;
import net.yt.libs.permissions.PermissionRequest;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        $(R.id.push_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(v.getId() == R.id.push_btn){
                    Intent intent = new Intent(MainActivity.this, PushActivity.class);
                    startActivity(intent);
                }
            }
        });

        //testPermissions();
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