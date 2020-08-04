package net.yt.lib.sdk.base;

public abstract class BaseConfig {

    //是否测试环境，返回true表示是测试环境，否则正式环境
    public abstract boolean isDebug();

    //返回app的文件根目录
    public abstract String rootPath();

}
