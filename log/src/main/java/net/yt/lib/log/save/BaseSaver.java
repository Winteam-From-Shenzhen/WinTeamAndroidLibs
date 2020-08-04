package net.yt.lib.log.save;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import net.yt.lib.log.encryption.IEncryption;
import net.yt.lib.log.util.Logs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 提供通用的保存操作log的日志和设备信息的方法
 * Created by czm.
 */
public abstract class BaseSaver implements ISave {

    private final static String TAG = "BaseSaver";

    /**
     * 使用线程池对异步的日志写入做管理，提高性能
     */
    public ExecutorService mThreadPool = Executors.newFixedThreadPool(1);

    /**
     * 根据日期创建文件夹,文件夹的名称以日期命名,下面是日期的格式
     */
    public final static SimpleDateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * 在每一条log前面增加一个时间戳
     */
    public final static SimpleDateFormat yyyy_MM_dd_HH_mm_ss_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS", Locale.getDefault());


    /**
     * 日志的保存的类型
     */
    public static final String SAVE_FILE_TYPE = ".txt";

    public static String TimeLogFolder;


    public static String LogFolder = net.yt.lib.log.Log.getInstance().getROOT() + "/Logs/";

    public Context mContext;

    /**
     * 加密方式
     */
    public static IEncryption mEncryption;

    public BaseSaver(Context context) {
        this.mContext = context;
    }

    /**
     * 用于在每条log前面，增加更多的文本信息，包括时间，线程名字等等
     */
    public static String formatLogMsg(String tag, String tips) {
        String timeStr = yyyy_MM_dd_HH_mm_ss_SS.format(Calendar.getInstance().getTime());
        Thread currThread = Thread.currentThread();
        StringBuilder sb = new StringBuilder();
        sb.append(timeStr)
                .append(" ")
                .append(currThread.getId())
                .append(" ")
                .append(tag)
                .append(" > ")
                .append(tips);
        //Logs.d("添加的内容是:\n" + sb.toString());
        return sb.toString();
    }

    /**
     * 写入设备的各种参数信息之前，请确保File文件以及他的父路径是存在的
     *
     * @param file 需要创建的文件
     */
    public File createFile(File file, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Application Information").append('\n');
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = context.getApplicationInfo();
        sb.append("App Name : ").append(pm.getApplicationLabel(ai)).append('\n');
        try {
            PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
            sb.append("Version Code: ").append(pi.versionCode).append('\n');
            sb.append("Version Name: ").append(pi.versionName).append('\n');
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append('\n');
        sb.append("DEVICE INFORMATION").append('\n');
        sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n');
        sb.append("BRAND: ").append(Build.BRAND).append('\n');
        sb.append("DEVICE: ").append(Build.DEVICE).append('\n');
        sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n').append('\n');


        Logs.d("创建的设备信息（加密前） = \n" + sb.toString());
        //加密信息
        sb = new StringBuilder(encodeString(sb.toString()));
        Logs.d("创建的设备信息（加密后） = \n" + sb.toString());
        try {
            if (!file.exists()) {
                boolean successCreate = file.createNewFile();
                if (!successCreate) {
                    return null;
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    @Override
    public void setEncodeType(IEncryption encodeType) {
        mEncryption = encodeType;
    }

    public String encodeString(String content) {
        if (mEncryption != null) {
            try {
                return mEncryption.encrypt(content);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return content;
            }
        }

        return content;

    }

    public String decodeString(String content) {
        if (mEncryption != null) {
            try {
                return mEncryption.decrypt(content);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return content;
            }
        }
        return content;
    }

    /**
     * 异步操作，务必加锁
     *
     * @param tag     Log的标签
     * @param content Log的内容
     */
    @Override
    public void writeLog(final String tag, final String content) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (BaseSaver.class) {
                    TimeLogFolder = net.yt.lib.log.Log.getInstance().getROOT() + "/Logs/" + yyyy_mm_dd.format(new Date(System.currentTimeMillis())) + "/";
                    final File logsDir = new File(TimeLogFolder);
                    final File logFile = new File(logsDir, "MonitorLog" + yyyy_mm_dd.format(new Date(System.currentTimeMillis())) + SAVE_FILE_TYPE);
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Logs.d("SDcard 不可用");
                        return;
                    }
                    if (!logsDir.exists()) {
                        Logs.d("logsDir.mkdirs() =  +　" + logsDir.mkdirs());
                    }
                    if (!logFile.exists()) {
                        createFile(logFile, mContext);
                    }
                    //long startTime = System.nanoTime();
                    //long endTime = System.nanoTime();
                    //Logs.d("wenming", "解密耗时为 = ： " + String.valueOf((double) (endTime - startTime) / 1000000) + "ms");
                    //Logs.d("wenming", "读取本地的Log文件，并且解密 = \n" + preContent.toString());
                    //Logs.d("wenming", "即将保存的Log文件内容 = \n" + preContent.toString());
                    //writeText(logFile, decodeString(FileUtil.getText(logFile)) + formatLogMsg(tag, content) + "\n");
                    writeText(logFile, formatLogMsg(tag, content) + "\n");
                }

            }
        });
    }

    public void writeText(final File logFile, final String content) {
        FileOutputStream outputStream = null;
        try {
            String encoderesult = encodeString(content);
            Logs.d("最终写到文本的Log：\n" + content);
            outputStream = new FileOutputStream(logFile, true);
            outputStream.write(encoderesult.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //每次写入的最后检测sd卡空间是否够用
            net.yt.lib.log.Log.getInstance().checkCacheSizeAndDelOldestFile(new File(net.yt.lib.log.Log.getInstance().getROOT()));
        }
    }
}
