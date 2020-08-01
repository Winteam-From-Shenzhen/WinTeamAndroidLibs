# WinTeamAndroidLibs

 云天智能终端 开源项目合集

 [![jitpack.io](https://jitpack.io/v/Winteam-From-Shenzhen/WinTeamAndroidLibs.svg)](https://jitpack.io/#Winteam-From-Shenzhen/WinTeamAndroidLibs)



## 添加依赖

**1、 根目录 build.gradle**

    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }

**2、app 目录下 build.gradle**

    两种方式：
    //全部依赖
    implementation 'com.github.Winteam-From-Shenzhen:WinTeamAndroidLibs:0.0.2'

    //单独依赖
    implementation 'com.github.Winteam-From-Shenzhen.WinTeamAndroidLibs:permissions:0.0.2'
    implementation 'com.github.Winteam-From-Shenzhen.WinTeamAndroidLibs:net:0.0.2'

## 目前支持的 Libs
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


### 2、网络框架
 基于 `Retrofit` 封装的一个简单网络请求框架，使用方式大致遵循`Retrofit`的原始使用方式
#### 1、普通网络请求

 **定义Api  interface**


    public interface AppApiInterface {

        @FormUrlEncoded
        @POST("app/weather/query_real-time_weather")
        Call<BaseResult<String>> getWeatherInfo(@Field("city") String productKey);
    }


 **定义Api Service**


    public class AppApi extends BaseApi<AppApiInterface> {
        private static AppApi appApi = null;
    
        //推荐使用 单例模式
        public static AppApi getInstance() {
            if (appApi == null) {
                synchronized (AppApi.class) {
                    if (appApi == null) {
                        appApi = new AppApi();
                    }
                }
            }
            return appApi;
        }
    
        private AppApi() {
            super("https://xxxxx/");
        }
    
        /**
         * 获取天气信息
         *
         * @param city           地址
         * @param commonCallback callback
         */
        public void getWeather(String city, CommonCallback<BaseResult<String>> commonCallback) {
            getApiInterface().getWeatherInfo(city).enqueue(commonCallback);
        }
    
            
        @Override
        public String getToken() {
            //如果需要 token ，在这里设置 token    
            return null;
        }
    }


**使用**

     AppApi.getInstance().getWeather("深圳",commonCallback);


#### 下载请求：

    DownLoadRequest downLoadRequest = new DownLoadRequest(fileUrl);
    downLoadRequest.startDownLoad(saveFullPath,callback);
