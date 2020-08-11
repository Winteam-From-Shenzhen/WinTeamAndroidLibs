package net.yt.lib.jpush;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import net.yt.lib.log.L;
import net.yt.lib.push.IOperateCallback;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

public class TagAliasManager {
    private static volatile int sSequence = 0;
    private final long TIME_OUT = 10 * 1000L; // 超时时间
    private HandlerThread mHandlerThread = null;
    private Handler mChildHandler = null;
    private HashMap<Integer, TagAliasAction> mActionsHM;
    private final Object mLocker = new Object();

    private synchronized static int createSequence(){
        return ++sSequence;
    }

    public void init(){
        synchronized (mLocker) {
            mHandlerThread = new HandlerThread("PushThread");
            mHandlerThread.start();
            mChildHandler = new Handler(mHandlerThread.getLooper());
            mActionsHM = new HashMap<Integer, TagAliasAction>();
        }
    }

    public void uninit(){
        synchronized (mLocker) {
            mChildHandler.removeCallbacksAndMessages(null);
            mChildHandler = null;
            mHandlerThread.quit();
            mHandlerThread = null;
            mActionsHM.clear();
            mActionsHM = null;
        }
    }

    public void setAlias(Context context, String alias, IOperateCallback cb) {
        synchronized (mLocker) {
            int sequence = createSequence();
            TagAliasAction action = new TagAliasAction(this, sequence, cb);
            mActionsHM.put(sequence, action);
            JPushInterface.setAlias(context, sequence, alias);
            mChildHandler.postDelayed(action, TIME_OUT);
        }
    }

    public void clearAlias(Context context, IOperateCallback cb) {
        synchronized (mLocker) {
            int sequence = createSequence();
            TagAliasAction action = new TagAliasAction(this, sequence, cb);
            mActionsHM.put(sequence, action);
            JPushInterface.deleteAlias(context, sequence);
            mChildHandler.postDelayed(action, TIME_OUT);
        }
    }

    public void setTags(Context context, String tag, IOperateCallback cb) {
        synchronized (mLocker) {
            int sequence = createSequence();
            TagAliasAction action = new TagAliasAction(this, sequence, cb);
            mActionsHM.put(sequence, action);
            Set<String> tagSet = getInPutTags(tag);
            if(null == tagSet){
                cb.fail();
                return;
            }
            JPushInterface.setTags(context, sequence, tagSet);
            mChildHandler.postDelayed(action, TIME_OUT);
        }
    }

    public void clearTags(Context context, IOperateCallback cb) {
        synchronized (mLocker) {
            int sequence = createSequence();
            TagAliasAction action = new TagAliasAction(this, sequence, cb);
            mActionsHM.put(sequence, action);
            JPushInterface.cleanTags(context, sequence);
            mChildHandler.postDelayed(action, TIME_OUT);
        }
    }

    public void timeoutAction(int sequence){
        synchronized (mLocker) {
            TagAliasAction a = mActionsHM.get(sequence);
            if(null != a) {
                a.cb.fail();
                mActionsHM.remove(sequence);
            }
        }
    }

    public void failAction(int sequence){
        synchronized (mLocker) {
            TagAliasAction a = mActionsHM.get(sequence);
            if(null != a) {
                a.cb.fail();
                mActionsHM.remove(sequence);
            }
        }
    }

    public void sucessAction(int sequence){
        synchronized (mLocker) {
            TagAliasAction a = mActionsHM.get(sequence);
            if(null != a) {
                a.cb.sucess();
                mActionsHM.remove(sequence);
            }
        }
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    private static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    private Set<String> getInPutTags(String tags){
        // ","隔开的多个 转换成 Set
        String[] sArray = tags.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!isValidTagAndAlias(sTagItme)) {
                L.e("JPush TagAliasManager getInPutTags  isValidTagAndAlias in " + tags);
                return null;
            }
            tagSet.add(sTagItme);
        }
        if(tagSet.isEmpty()){
            L.e("JPush TagAliasManager getInPutTags  tags is empty !");
            return null;
        }
        return tagSet;
    }
}
