package net.yt.lib.log.save;

import net.yt.lib.log.encryption.IEncryption;

/**
 * 保存日志与崩溃信息的接口
 * Created by czm.
 */
public interface ISave {

    void writeLog(String tag, String content);

    void writeCrash(Thread thread, Throwable ex, String tag, String content);

    void setEncodeType(IEncryption encodeType);

}
