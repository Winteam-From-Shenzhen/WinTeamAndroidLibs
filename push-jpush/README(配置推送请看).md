极光推送配置：
只需要在根项目的build.gradle文件里 添加下面的 manifestPlaceholders 的内容即可
defaultConfig {
        //略...
        //极光推送
        manifestPlaceholders = [
                JPUSH_PKGNAME : "你的包名",             //主项目包名，此包名需要在极光开发者平台设置过
                JPUSH_APPKEY : "你的key",              //JPush 上注册的包名对应的 Appkey.
                JPUSH_CHANNEL : "developer-default",  //暂时填写默认值即可.（不管）
        ]

}