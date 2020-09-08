package net.yt.lib.ota;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.equals(action, DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            return;
        }
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (downloadId == -1) {
            return;
        }
        DownloadManager downloadManager = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        int status = getDownloadStatus(downloadManager, downloadId);

        if (status != DownloadManager.STATUS_SUCCESSFUL) { //下载状态不等于成功就跳出
            return;
        }
        Uri uri = downloadManager.getUriForDownloadedFile(downloadId);//获取下载完成文件uri
        if (uri == null) {
            return;
        }
        installApk(context, uri);
    }

    /**
     * 安装apk方法
     *
     * @param context
     * @param uri
     */
    private void installApk(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");

        }
        context.startActivity(intent);
    }

    /**
     * 获取下载状态
     *
     * @param downloadManager
     * @param downloadId
     * @return
     */
    private int getDownloadStatus(DownloadManager downloadManager, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
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

}
