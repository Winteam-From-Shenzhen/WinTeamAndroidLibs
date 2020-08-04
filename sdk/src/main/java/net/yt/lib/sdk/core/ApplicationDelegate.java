package net.yt.lib.sdk.core;

import android.app.Application;
import android.content.Context;

import net.yt.lib.sdk.utils.ManifestParser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplicationDelegate {
    private  List<IAppLife> mList;

    public ApplicationDelegate(Context base) {
        mList = new ManifestParser(base).parse();
        //按照优先级排序
        Collections.sort(mList, new Comparator<IAppLife>() {
            @Override
            public int compare(IAppLife o1, IAppLife o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
    }

    public void attachBaseContext(Context base) {
        if (mList != null && mList.size() > 0) {
            for (IAppLife life : mList) {
                life.attachBaseContext(base);
            }
        }
    }

    public void onCreate(Application application) {
        if (mList != null && mList.size() > 0) {
            for (IAppLife life : mList) {
                life.onCreate(application);
            }
        }
    }

    public void onTerminate(Application application) {
        if (mList != null && mList.size() > 0) {
            for (IAppLife life : mList) {
                life.onTerminate(application);
            }
        }
        mList.clear();
        mList = null;
    }
}
