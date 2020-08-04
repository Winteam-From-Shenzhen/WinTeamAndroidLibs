package net.yt.cmd;

import net.yt.util.SerialPortUtils;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 16:43
 * Package name : net.yt.cmd
 * Des :
 */
public class SendMessage implements IMessage {

    private String message;
    private byte[] bytes;

    //标识是否发送完成，或者失败，默认成功
    private boolean isSend = true;
    private String port;

    //如果使能，则表示是结束的信息，接收到的阻塞队列将会结束退出
    private boolean isStopFlag = false;


    public SendMessage(String port, byte[] bytes) {
        this.port = port;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getPort() {
        return port;
    }

    @Override
    public String getMessage() {
        message = SerialPortUtils.ByteArrToHex(bytes);
        return message;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setStopFlag(boolean flag) {
        isStopFlag = flag;
    }

    public boolean isStopFlag() {
        return isStopFlag;
    }
}
