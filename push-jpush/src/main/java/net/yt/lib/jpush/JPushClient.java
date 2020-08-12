package net.yt.lib.jpush;

import android.content.Context;

import net.yt.lib.log.L;
import net.yt.lib.push.IOperateCallback;
import net.yt.lib.push.IPushClient;
import net.yt.lib.push.IPushReceiver;
import net.yt.lib.push.Message;
import net.yt.lib.push.Target;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;

public class JPushClient implements IPushClient {
    private IPushReceiver mPushReceiver;
    private static JPushClient sPushClient;
    private Context mContext;
    private TagAliasManager mTagAliasManager;

    public static JPushClient getPushClient(){
        synchronized (JPushClient.class){
            return sPushClient;
        }
    }

    @Override
    public void init(Context context, boolean isDebug, IPushReceiver receiver) {
        if(null == context || null == receiver){
            throw new IllegalArgumentException("JPUSH init context or receiver is empty ! ");
        }

        synchronized (JPushClient.class) {
            JPushInterface.setDebugMode(isDebug);
            JPushInterface.init(context);
            mContext = context;
            mPushReceiver = receiver;
            mTagAliasManager = new TagAliasManager();
            mTagAliasManager.init();
            sPushClient = this;
        }
    }

    @Override
    public void setAlias(String alias, IOperateCallback cb) {
        mTagAliasManager.setAlias(mContext, alias, cb);
    }

    @Override
    public void clearAlias(IOperateCallback cb) {
        mTagAliasManager.clearAlias(mContext, cb);
    }

    @Override
    public void setTags(String tags, IOperateCallback cb) {
        mTagAliasManager.setTags(mContext, tags, cb);
    }

    @Override
    public void clearTags(IOperateCallback cb) {
        mTagAliasManager.clearTags(mContext, cb);
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage){
        int sequence = jPushMessage.getSequence();
        L.i("JPushClient action - onAliasOperatorResult, sequence:"+sequence
                +",alias:"+jPushMessage.getAlias()
                +",result:"+(jPushMessage.getErrorCode()));
        if(jPushMessage.getErrorCode() == 0) {
            mTagAliasManager.sucessAction(sequence);
        }else{
            mTagAliasManager.failAction(sequence);
        }
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage){
        int sequence = jPushMessage.getSequence();
        L.i("JPushClient action - onTagOperatorResult, sequence:"+sequence
                +",tag:"+jPushMessage.getTags()
                +",result:"+(jPushMessage.getErrorCode()));
        if(jPushMessage.getErrorCode() == 0) {
            mTagAliasManager.sucessAction(sequence);
        }else{
            mTagAliasManager.failAction(sequence);
        }
    }

    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Message msg = new Message();
        msg.setTitle(message.notificationTitle);
        msg.setMessage(message.notificationContent);
        msg.setExtra(message.notificationExtras);
        msg.setMessageID(message.msgId);
        msg.setNotifyID(message.notificationId);
        msg.setTarget(Target.JPUSH);
        mPushReceiver.onReceiveNotification(context, msg);
    }

    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Message msg = new Message();
        msg.setTitle(message.notificationTitle);
        msg.setMessage(message.notificationContent);
        msg.setExtra(message.notificationExtras);
        msg.setMessageID(message.msgId);
        msg.setNotifyID(message.notificationId);
        msg.setTarget(Target.JPUSH);
        mPushReceiver.onReceiveNotificationClick(context, msg);
    }

    public void onMessage(final Context context, final CustomMessage customMessage) {
        Message msg = new Message();
        msg.setTitle(customMessage.title);
        msg.setMessage(customMessage.message);
        msg.setExtra(customMessage.extra);
        msg.setMessageID(customMessage.messageId);
        msg.setTarget(Target.JPUSH);
        mPushReceiver.onReceiveMessage(context, msg);
    }

}
