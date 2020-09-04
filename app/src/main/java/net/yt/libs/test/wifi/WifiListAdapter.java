package net.yt.libs.test.wifi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.yt.lib.wifi.Constant;
import net.yt.lib.wifi.WifiBean;
import net.yt.lib.wifi.WifiTool;
import net.yt.libs.test.R;

import java.util.ArrayList;
import java.util.List;

class WifiListAdapter extends BaseAdapter {
    private List<WifiBean> mWifiList;
    private LayoutInflater mLayoutInflater;
    private String mConnectingSSID;

    public WifiListAdapter(LayoutInflater layoutInflater) {
        this.mWifiList = new ArrayList<WifiBean>();
        this.mLayoutInflater = layoutInflater;
    }

    public void updateData(List<WifiBean> data){
        mWifiList.clear();
        mWifiList.addAll(data);
        this.notifyDataSetChanged();
    }

    public void setConnectingSSID(String ssid){
        mConnectingSSID = ssid;
    }

    @Override
    public int getCount() {
        return mWifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_wifi_list2, null);
            holder.levelIv = convertView.findViewById(R.id.img_wifi_level);
            holder.nameTv = convertView.findViewById(R.id.tv_wifi_name);
            holder.stateTv = convertView.findViewById(R.id.tv_wifi_state);
            holder.lockIv = convertView.findViewById(R.id.img_wifi_lock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WifiBean sr = mWifiList.get(position);
        int rssiLevel = sr.level;
        if(0 == rssiLevel) {
            holder.levelIv.setImageResource(R.mipmap.icon_wifi_level_1);
        }else if(1 == rssiLevel) {
            holder.levelIv.setImageResource(R.mipmap.icon_wifi_level_1);
        }else if(2 == rssiLevel) {
            holder.levelIv.setImageResource(R.mipmap.icon_wifi_level_2);
        }else if(3 == rssiLevel) {
            holder.levelIv.setImageResource(R.mipmap.icon_wifi_level_3);
        }else if(4 == rssiLevel) {
            holder.levelIv.setImageResource(R.mipmap.icon_wifi_level_4);
        }
        holder.nameTv.setText(sr.SSID);
        if(sr.SSID.equals(mConnectingSSID)) {
            holder.stateTv.setVisibility(View.VISIBLE);
            holder.stateTv.setText("连接中");
        }else if(WifiTool.I().isSsidSaved(sr.SSID) == WifiTool.SSID_STATE.SSID_STATE_EXIST_BUT_DISCONNECTED){
            holder.stateTv.setVisibility(View.VISIBLE);
            holder.stateTv.setText("已断开");
        }else if(WifiTool.I().isSsidSaved(sr.SSID) == WifiTool.SSID_STATE.SSID_STATE_EXIST_AND_CONNECTED){
            holder.stateTv.setVisibility(View.VISIBLE);
            holder.stateTv.setText("已保存");
        }else{
            holder.stateTv.setVisibility(View.GONE);
        }
        if (sr.security == Constant.SECURITY_NONE){
            holder.lockIv.setVisibility(View.INVISIBLE);
        }else{
            holder.lockIv.setVisibility(View.VISIBLE);
            holder.lockIv.setImageResource(R.mipmap.icon_wifi_lock);
        }

        return convertView;
    }

    public static class ViewHolder{
        public ImageView levelIv;
        public TextView nameTv;
        public TextView stateTv;
        public ImageView lockIv;
    }
}
