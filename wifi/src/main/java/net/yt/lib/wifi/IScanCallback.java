package net.yt.lib.wifi;

import java.util.List;

public interface IScanCallback {
    void onSucess(List<WifiBean> result);
    void onFail(String error);
}
