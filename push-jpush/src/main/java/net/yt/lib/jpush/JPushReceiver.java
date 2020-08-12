package net.yt.lib.jpush;

import android.content.Context;
import android.content.Intent;

import net.yt.lib.log.L;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;

public class JPushReceiver extends cn.jpush.android.service.JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onMessage(final Context context, final CustomMessage customMessage) {
        //让JpushClient来处理
        JPushClient client = JPushClient.getPushClient();
        if(null != client){
            client.onMessage(context, customMessage);
        }
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        //点击通知回调
        //让JpushClient来处理
        JPushClient client = JPushClient.getPushClient();
        if(null != client){
            client.onNotifyMessageOpened(context, message);
        }
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        //通知的MultiAction回调
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        //收到通知回调
        //让JpushClient来处理
        JPushClient client = JPushClient.getPushClient();
        if(null != client){
            client.onNotifyMessageArrived(context, message);
        }
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        //清除通知回调
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        //注册成功回调
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        //长连接状态回调
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        //注册失败回调
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        //让JpushClient来处理
        JPushClient client = JPushClient.getPushClient();
        if(null != client){
            client.onTagOperatorResult(context, jPushMessage);
        }
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        //让JpushClient来处理
        L.i(" ***************  onAliasOperatorResult " + jPushMessage.toString());
        JPushClient client = JPushClient.getPushClient();
        if(null != client){
            client.onAliasOperatorResult(context, jPushMessage);
        }
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        //通知开关的回调
    }

}
