package net.yt.whale.net;

/**
 * Auth : xiao.yunfei
 * Date : 2020/7/21 13:50
 * Package name : net.yt.whale.net
 * Des : 网络连接配置
 */
public final class Config {

    private static int CONNECT_TIME = 5; //连接超时
    private static int READ_TIME = 5; //读取超时
    private static int WRITE_TIME = 5; //写入超时

    private static boolean isDebug = true; //是否开启调试信息

    public static int getConnectTime() {
        return CONNECT_TIME;
    }


    public static int getReadTime() {
        return READ_TIME;
    }


    public static int getWriteTime() {
        return WRITE_TIME;
    }


    public static boolean isIsDebug() {
        return isDebug;
    }

    public final class ConfigSetting {
        private int connectTimeout = 5;
        private int readTimeout = 5;
        private int writeTimeout = 5;

        private boolean isDebug = true;


        public ConfigSetting isDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public ConfigSetting setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public ConfigSetting setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public ConfigSetting setWriteTimeout(int writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public void updateConfig() {
            CONNECT_TIME = connectTimeout;
            WRITE_TIME = writeTimeout;
            READ_TIME = readTimeout;
            Config.isDebug = isDebug;
        }
    }

}
