package net.yt.impl;

import net.yt.cmd.ReceiverMessage;
import net.yt.cmd.SendMessage;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 10:58
 * Package name : net.yt.impl
 * Des :
 */
public interface OnDataReceive {

    void onDataReceiver(String path , ReceiverMessage receiverMessage);

    void onSendCommand(SendMessage sendMessage);
}
