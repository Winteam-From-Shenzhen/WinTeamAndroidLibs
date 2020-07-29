package net.yt.libs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;

import com.shon.permissions.OnPermissionCallback;
import com.shon.permissions.PermissionCheck;
import com.shon.permissions.PermissionRequest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        testPermissions();
    }

    private void testPermissions() {
        boolean canReadStorage = PermissionCheck.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (canReadStorage) {
            return;
        }
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