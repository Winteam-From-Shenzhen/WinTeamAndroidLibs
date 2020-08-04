package net.yt.lib.sdk.work;

import android.content.Context;

public interface IWork {
    void onInit(Context c);
    void onUninit(Context c);
    String getName();
}
