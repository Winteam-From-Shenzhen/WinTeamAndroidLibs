package net.yt.lib.log;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import net.yt.lib.log.crash.CrashHandler;
import net.yt.lib.log.encryption.IEncryption;
import net.yt.lib.log.save.BaseSaver;
import net.yt.lib.log.save.ISave;
import net.yt.lib.log.save.imp.CrashWriter;
import net.yt.lib.log.save.imp.LogWriter;
import net.yt.lib.log.upload.ILogUpload;
import net.yt.lib.log.upload.UploadService;
import net.yt.lib.log.util.FileUtil;
import net.yt.lib.log.util.Logs;
import net.yt.lib.log.util.NetUtil;

import java.io.File;

/**
 * 日志崩溃管理框架
 */
public class Log {

    private static Log mLogUtil;
    /**
     * 设置上传的方式
     */
    public ILogUpload mUpload;
    /**
     * 设置缓存文件夹的大小,默认是50MB
     */
    private long mCacheSize = 50 * 1024 * 1024;

    /**
     * 设置日志保存的路径
     */
    private String mROOT;

    /**
     * 设置加密方式
     */
    private IEncryption mEncryption;

    /**
     * 设置日志的保存方式
     */
    private ISave mLogSaver;

    /**
     * 设置在哪种网络状态下上传，true为只在wifi模式下上传，false是wifi和移动网络都上传
     */
    private boolean mWifiOnly = false;

    //LOG 级别
    public static int LOG_LEVEL_V = 0;
    public static int LOG_LEVEL_D = 1;
    public static int LOG_LEVEL_I = 2;
    public static int LOG_LEVEL_W = 3;
    public static int LOG_LEVEL_E = 4;
    //开启的LOG级别
    private int mLevel = LOG_LEVEL_V;

    //是否开启
    private boolean mEnable = true;

    //标签
    private String mTag = "ytzn";

    //是否保存到文件
    private boolean mIsSaveToFile = true;

    //初始化结束
    private boolean mHasInit = false;

    private Log() {
    }

    public static Log getInstance() {
        if (mLogUtil == null) {
            synchronized (Log.class) {
                if (mLogUtil == null) {
                    mLogUtil = new Log();
                }
            }
        }
        return mLogUtil;
    }

    public Log setCacheSize(long cacheSize) {
        this.mCacheSize = cacheSize;
        return this;
    }

    public long getCacheSize() {
        return mCacheSize;
    }

    public Log setEncryption(IEncryption encryption) {
        this.mEncryption = encryption;
        return this;
    }

    public Log setUploadType(ILogUpload logUpload) {
        mUpload = logUpload;
        return this;
    }

    public ILogUpload getUpload() {
        return mUpload;
    }

    public Log setWifiOnly(boolean wifiOnly) {
        mWifiOnly = wifiOnly;
        return this;
    }

    public Log setLeve(int logLeve){
        mLevel = logLeve;
        return this;
    }

    public int getLogLeve(){
        return mLevel;
    }


    public Log setEnable(boolean enable){
        mEnable = enable;
        return this;
    }

    public boolean getEnable(){
        return this.mEnable;
    }

    public Log setTag(String tag){
        mTag = tag;
        return this;
    }

    public Log setIsSaveLogToFile(boolean enable){
        mIsSaveToFile = enable;
        return this;
    }

    public Log setLogDir(Context context, String logDir) {
        if (TextUtils.isEmpty(logDir)) {
            //如果SD不可用，则存储在沙盒中
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mROOT = context.getExternalCacheDir().getAbsolutePath();
            } else {
                mROOT = context.getCacheDir().getAbsolutePath();
            }
        } else {
            mROOT = logDir;
        }
        return this;
    }

    public Log setLogSaver(ISave logSaver) {
        this.mLogSaver = logSaver;
        return this;
    }

    public  Log setLogDebugModel(boolean isDebug){
        Logs.isDebug = isDebug;
        return this;
    }

    public String getROOT() {
        return mROOT;
    }

    public synchronized void init(Context context) {
        if (TextUtils.isEmpty(mROOT)) {
            //如果SD不可用，则存储在沙盒中
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mROOT = context.getExternalCacheDir().getAbsolutePath();
            } else {
                mROOT = context.getCacheDir().getAbsolutePath();
            }
        }
        Logs.d("Log mROOT = " + mROOT);
        if(null == mLogSaver){
            mLogSaver = new CrashWriter(context);
        }
        if (mEncryption != null) {
            mLogSaver.setEncodeType(mEncryption);
        }
        CrashHandler.getInstance().init(mLogSaver);
        LogWriter.getInstance().init(mLogSaver);

        mHasInit = true;
    }

    /**
     * 检查文件夹是否超出缓存大小，超出则会删除该目录下的所有文件
     *
     * @param dir 需要检查大小的文件夹
     * @return 返回是否超过大小，true为是，false为否
     */

    public synchronized boolean checkCacheSizeAndDelOldestFile(File dir) {
        if(!mHasInit){
            return false;
        }

        long dirSize = FileUtil.folderSize(dir);
        return dirSize >= Log.getInstance().getCacheSize() && FileUtil.deleteOldestFile(new File(BaseSaver.LogFolder));
    }

    /**
     * 调用此方法，上传日志信息
     *
     * @param applicationContext 全局的application context，避免内存泄露
     */
    public void upload(Context applicationContext) {
        if(!mHasInit){
            return;
        }

        //如果没有设置上传，则不执行
        if (mUpload == null) {
            return;
        }
        //如果网络可用，而且是移动网络，但是用户设置了只在wifi下上传，返回
        if (NetUtil.isConnected(applicationContext) && !NetUtil.isWifi(applicationContext) && mWifiOnly) {
            return;
        }
        Intent intent = new Intent(applicationContext, UploadService.class);
        applicationContext.startService(intent);
    }


    /************************************************************************************************************************/

    public void v(String msg) {
        if (mEnable && mLevel <= LOG_LEVEL_V) {
            if (mHasInit && mIsSaveToFile){
                LogWriter.writeLog("V", msg);
            }
            android.util.Log.v(mTag, mTag + " " + msg);
        }
    }

    public void vv(String key, String msg) {
        this.v("" + key + " " + msg);
    }

    public void d(String msg) {
        if (mEnable && mLevel <= LOG_LEVEL_V) {
            if (mHasInit && mIsSaveToFile){
                LogWriter.writeLog("D", msg);
            }
            android.util.Log.d(mTag, mTag + " " + msg);
        }
    }

    public void dd(String key, String msg) {
        this.d("" + key + " " + msg);
    }

    public void i(String msg) {
        if (mEnable && mLevel <= LOG_LEVEL_V) {
            if (mHasInit && mIsSaveToFile){
                LogWriter.writeLog("I", msg);
            }
            android.util.Log.i(mTag, mTag + " " + msg);
        }
    }

    public void ii(String key, String msg) {
        this.i("" + key + " " + msg);
    }

    public void w(String msg) {
        if (mEnable && mLevel <= LOG_LEVEL_V) {
            if (mHasInit && mIsSaveToFile){
                LogWriter.writeLog("W", msg);
            }
            android.util.Log.w(mTag, mTag + " " + msg);
        }
    }

    public void ww(String key, String msg) {
        this.w("" + key + " " + msg);
    }

    public void e(String msg) {
        if (mEnable && mLevel <= LOG_LEVEL_V) {
            if (mHasInit && mIsSaveToFile){
                LogWriter.writeLog("E", msg);
            }
            android.util.Log.e(mTag, mTag + " " + msg);
        }
    }

    public void ee(String key, String msg) {
        this.e("" + key + " " + msg);
    }

}
