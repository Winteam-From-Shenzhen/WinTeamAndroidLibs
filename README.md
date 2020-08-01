# WinTeamAndroidLibs

 云天智能终端 开源项目合集

### 1、permissions
 权限申请工具

 [![jitpack.io](https://jitpack.io/v/Winteam-From-Shenzhen/WinTeamAndroidLibs.svg)](https://jitpack.io/#Winteam-From-Shenzhen/WinTeamAndroidLibs)

**1、添加依赖**
1、 根目录 build.gradle

    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }

2、app 目录下 build.gradle

    两种方式：
    //全部依赖
    implementation 'com.github.Winteam-From-Shenzhen:WinTeamAndroidLibs:0.0.2'

    //单独依赖
    implementation 'com.github.Winteam-From-Shenzhen.WinTeamAndroidLibs:permissions:0.0.2'
    implementation 'com.github.Winteam-From-Shenzhen.WinTeamAndroidLibs:net:0.0.2'


 **2、使用方式**


        // 1、检查是否已获取某一权限    
        boolean canReadStorage = PermissionCheck.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (canReadStorage) {
            return;
        }
    
        // 2、检查使用已获取 某些权限
        PermissionCheck.hasPerMissions(this, new String[]{}, new OnPermissionCallback() {
            @Override
            public void onRequest(boolean granted, @Nullable String[] reRequest) {
                // granted 为true ,表示已全部允许，
                // granted 为false ,请 检查 reRequest ，reRequest表示 需要再次申请的权限           
                
            }
        });
            
        //申请权限        
        PermissionRequest permissionRequest = new PermissionRequest.Builder(this)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setCallback(new OnPermissionCallback() {
                    @Override
                    public void onRequest(boolean granted, @Nullable String[] reRequest) {

                    }
                })
                .build();

        permissionRequest.requestPermissions();
