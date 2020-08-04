package net.yt.lib.log.save.imp;

import net.yt.lib.log.Log;
import net.yt.lib.log.save.ISave;
import net.yt.lib.log.util.Logs;

/**
 * 用于写入Log到本地
 * Created by czm.
 */
public class LogWriter {
    private static LogWriter mLogWriter;
    private static ISave mSave;

    private LogWriter() {
    }


    public static LogWriter getInstance() {
        if (mLogWriter == null) {
            synchronized (Log.class) {
                if (mLogWriter == null) {
                    mLogWriter = new LogWriter();
                }
            }
        }
        return mLogWriter;
    }


    public LogWriter init(ISave save) {
        mSave = save;
        return this;
    }

    public static void writeLog(String tag, String content) {
        Logs.d(tag, content);
        mSave.writeLog(tag, content);
    }
}