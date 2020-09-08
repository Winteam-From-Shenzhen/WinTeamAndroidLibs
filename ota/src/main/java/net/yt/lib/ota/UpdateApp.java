package net.yt.lib.ota;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.lang.ref.WeakReference;


public class UpdateApp {
    private Build mBuild;
    private long mDownloadId = -1;
    private DownloadManager mDownloadManager;
    private DownloadReceiver mAppDownReceiver;

    protected UpdateApp(Build build) {
        mBuild = build;

    }

    /**
     * 开始更新
     */
    public void startUpdate() {
        if (mAppDownReceiver != null) {
            mBuild.applicationWeakReference.get().unregisterReceiver(mAppDownReceiver);
        }
        mAppDownReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        mBuild.applicationWeakReference.get().registerReceiver(mAppDownReceiver, intentFilter);
        mDownloadManager = (DownloadManager) mBuild.applicationWeakReference.get().getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mBuild.url));
        if (mBuild.headerKey != null && mBuild.headerValue != null) {
            request.addRequestHeader(mBuild.headerKey, mBuild.headerValue);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mBuild.fileName);//设置下载文件保存目录与文件名称
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverMetered(true);
        request.setTitle(mBuild.title);
        mDownloadId = mDownloadManager.enqueue(request);
    }

    /**
     * 取消更新
     */
    public void cancelUpdate() {
        if (mDownloadId != -1 && mDownloadManager != null) {
            mDownloadManager.remove(mDownloadId);
        }
    }

    /**
     * 获取当前下载进度
     *
     * @return
     */
    public int getDownloadProgress() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownloadId);
        Cursor c = mDownloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                }
            } finally {
                c.close();
            }
        }
        return -1;

    }

    /**
     * 获取下载总大小
     *
     * @return
     */
    public int getDownloadTotal() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownloadId);
        Cursor c = mDownloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }

    /**
     * 获取下载状态
     *
     * @return  {@link DownloadManager.STATUS_PENDING}
     *          {@link DownloadManager.STATUS_RUNNING}
     *          {@link DownloadManager.STATUS_PAUSED}
     *          {@link DownloadManager.STATUS_SUCCESSFUL}
     *          {@link DownloadManager.STATUS_FAILED}
     */
    public int getDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownloadId);
        Cursor c = mDownloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAppDownReceiver != null) {
            mBuild.applicationWeakReference.get().unregisterReceiver(mAppDownReceiver);
            mAppDownReceiver = null;
        }
        mBuild.applicationWeakReference.clear();
        mBuild = null;
        mDownloadManager = null;
    }


    public static class Build {
        private WeakReference<Context> applicationWeakReference = null;
        private String url = null;
        private String fileName = null;
        private String title = null;
        private String headerKey = null;
        private String headerValue = null;


        public Build(Context context) {
            applicationWeakReference = new WeakReference<>(context);

        }

        /**
         * 设置下载地址
         *
         * @param url
         * @return
         */
        public Build setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置文件路径与名称
         *
         * @param fileName
         * @return
         */
        public Build setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 设置下载的状态栏标题
         *
         * @param title
         * @return
         */
        public Build setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置http头
         *
         * @param key
         * @param value
         * @return
         */
        public Build setRequestHeader(String key, String value) {
            this.headerKey = key;
            this.headerValue = value;
            return this;
        }


        public UpdateApp build() throws NullPointerException {
            if (url == null || fileName == null) {
                throw new NullPointerException();
            }
            return new UpdateApp(this);

        }
    }
}
