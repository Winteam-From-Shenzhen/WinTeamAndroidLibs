# WinTeamAndroidLibs

 云天智能终端 开源项目合集

### 1、permissions
 权限申请工具

 **使用方式**


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
