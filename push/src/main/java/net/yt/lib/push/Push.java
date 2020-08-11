package net.yt.lib.push;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import net.yt.lib.log.L;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Push {
    private static final String TAG = "Push";
    private static Push sInstance;

    //the meta-data header
    private static final String META_DATA_PUSH_HEADER = "YTPUSH_";
    //the meta_data split symbol
    public static final String METE_DATA_SPLIT_SYMBOL = "_";

    private IPushClient mIPushClient;

    //all support push platform map
    private LinkedHashMap<String, String> mAllSupportPushPlatformMap = new LinkedHashMap<>();

    public static Push I() {
        if(null == sInstance){
            synchronized (Push.class){
                sInstance = new Push();
            }
        }
        return sInstance;
    }

    private Push(){
    }

    public void init(Application application, String target, boolean debug, IPushReceiver receiver){
        initClient(application, target, debug, receiver);
    }

    private void initClient(Application application, String target, boolean debug, IPushReceiver receiver) {
        if(null == application || TextUtils.isEmpty(target)){
            throw new IllegalArgumentException("Push init client argument is error !");
        }

        Context context = application.getApplicationContext();
        try {
            //find all support push platform
            Bundle metaData = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (metaData != null) {
                Set<String> allKeys = metaData.keySet();
                if (allKeys != null && !allKeys.isEmpty()) {
                    Iterator<String> iterator = allKeys.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        if (key.startsWith(META_DATA_PUSH_HEADER)) {
                            mAllSupportPushPlatformMap.put(key, metaData.getString(key));
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (mAllSupportPushPlatformMap.isEmpty()) {
            throw new IllegalArgumentException("Push have none push platform,check AndroidManifest.xml is have meta-data name is start with PUSH_");
        }

        //choose custom push platform
        Iterator<Map.Entry<String, String>> iterator = mAllSupportPushPlatformMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String metaPlatformName = next.getKey();
            String metaPlatformClassName = next.getValue();
            StringBuilder stringBuilder = new StringBuilder(metaPlatformName).delete(0, 7);
            String platformName = stringBuilder.toString();
            if(!target.equals(platformName)){
                continue;
            }
            try {
                Class<?> currentClz = Class.forName(metaPlatformClassName);
                Class<?>[] interfaces = currentClz.getInterfaces();
                List<Class<?>> allInterfaces = Arrays.asList(interfaces);
                if (allInterfaces.contains(IPushClient.class)) {
                    //create object with no params
                    IPushClient iPushClient = (IPushClient) currentClz.newInstance();
                    this.mIPushClient = iPushClient;
                    //invoke IPushClient initContext method
                    L.i("Push current register platform is "+metaPlatformName);
                    iPushClient.init(application, debug, receiver);
                    break;
                } else {
                    throw new IllegalArgumentException(metaPlatformClassName + "is not implements " + IPushClient.class.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("can not find class " + metaPlatformClassName);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //clear cache client
        mAllSupportPushPlatformMap.clear();
        if (mIPushClient == null) {
            throw new IllegalStateException("onRegisterPush must at least one of them returns to true");
        }
    }

    /**
     * 设置别名，需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     * 每个用户只能指定一个别名。
     */
    public void setAlias(String alias, IOperateCallback cb){
        mIPushClient.setAlias(alias, cb);
    }

    public void clearAlias(IOperateCallback cb) {
        mIPushClient.clearAlias(cb);
    }

    /**
     * 设置标签，需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     * 标签之间用英文逗号隔开
     */
    public void setTags(String tags, IOperateCallback cb) {
        mIPushClient.setTags(tags, cb);
    }

    public void clearTags(IOperateCallback cb) {
        mIPushClient.clearTags(cb);
    }

}
