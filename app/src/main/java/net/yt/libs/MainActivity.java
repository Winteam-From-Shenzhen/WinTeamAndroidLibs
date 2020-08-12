package net.yt.libs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;

import net.yt.libs.permissions.OnPermissionCallback;
import net.yt.libs.permissions.PermissionCheck;
import net.yt.libs.permissions.PermissionRequest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        testPermissions();


        testLogin();
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

    private void testLogin() {

    }
}