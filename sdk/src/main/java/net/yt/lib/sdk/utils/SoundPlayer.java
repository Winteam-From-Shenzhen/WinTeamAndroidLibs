package net.yt.lib.sdk.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import net.yt.lib.log.L;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    private final String TAG = "SoundPlayer";

    private static SoundPlayer sInstance = null;

    private SoundPool mSoundPool = null;
    private HashMap<String, Integer> mSoundIdMap;

    private SoundPlayer(){
    }

    public static SoundPlayer I(){
        if(sInstance == null){
            synchronized (SoundPlayer.class){
                if(sInstance == null){
                    sInstance = new SoundPlayer();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context){
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build() ;
        mSoundPool =  new SoundPool.Builder()
                .setMaxStreams(10)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build() ;

        mSoundIdMap = new HashMap<String, Integer>();
        mSoundIdMap.clear();

        try {
            String[] files =  context.getAssets().list("raw");
            for(int i=0; i<files.length; i++){
                String fileName = files[i].substring(0, files[i].indexOf('.'));
                mSoundIdMap.put(fileName, mSoundPool.load(context.getAssets().openFd("raw/" + files[i]), 1));
                L.dd(TAG, "init " + i + " fileName = " + fileName + " file = " + files[i]);
            }
        }catch (IOException e){
            e.printStackTrace();
            L.ee(TAG, " list excepty " + e.getMessage());
        }
    }

    public void uninit(Context context){
        for(Map.Entry<String ,Integer> item : mSoundIdMap.entrySet()){
            mSoundPool.unload(item.getValue());
        }
        mSoundIdMap.clear();
        mSoundIdMap = null;
        mSoundPool.release();
        mSoundPool = null;
    }

    //返回非0表示播放成功
    public int alarm(String name){
        if(mSoundIdMap.containsKey(name)){
            return mSoundPool.play(mSoundIdMap.get(name), 1, 1, 1, 0, 1);
        }else{
            L.ee(TAG, "play name is not exist " + name);
            return -1;
        }
    }

    //返回非0表示播放成功
    public int alarmLoop(String name){
        if(mSoundIdMap.containsKey(name)){
            return mSoundPool.play(mSoundIdMap.get(name), 1, 1, 1, -1, 1);
        }else{
            L.ee(TAG, "play loop name is not exist " + name);
            return -1;
        }
    }

    public void stopAlarm(int streamId){
        if(streamId > 0) {
            mSoundPool.stop(streamId);
        }
    }
}
