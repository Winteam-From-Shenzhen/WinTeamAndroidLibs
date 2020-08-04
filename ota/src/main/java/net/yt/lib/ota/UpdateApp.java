package net.yt.lib.ota;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.lang.ref.WeakReference;


public class UpdateApp {
    private Build mBuild;
    private long mDownloadId = -1;
    private DownloadManager mDownloadManager;

    protected UpdateApp(Build build) {
        mBuild = build;
    }

    public void startUpdate() {
        DownloadReceiver appDownReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        mBuild.applicationWeakReference.get().registerReceiver(appDownReceiver,intentFilter);
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

    public void cancelUpdate() {
        if (mDownloadId != -1 && mDownloadManager != null) {
            mDownloadManager.remove(mDownloadId);
        }
    }

    public void release() {
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

        public Build setUrl(String url) {
            this.url = url;
            return this;
        }

        public Build setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Build setTitle(String title) {
            this.title = title;
            return this;
        }

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
