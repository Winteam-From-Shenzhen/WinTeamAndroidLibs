package net.yt.lib.push;

import android.content.Context;

public interface IPushReceiver {
    /**
     * When you received notice
     *
     * @param context
     * @param msg
     */
    void onReceiveNotification(Context context, Message msg);

    /**
     * When you received the notice by clicking
     *
     * @param context
     * @param msg
     */
    void onReceiveNotificationClick(Context context, Message msg);

    /**
     * When I received passthrough message
     *
     * @param context
     * @param msg
     */
    void onReceiveMessage(Context context, Message msg);

}
