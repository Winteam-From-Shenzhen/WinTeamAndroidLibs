package net.yt.cmd;

import net.yt.util.SerialPortUtils;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 11:19
 * Package name : net.yt.cmd
 * Des :
 */
public class ReceiverMessage implements IMessage {
    private String message;
    private byte[] bytes;

    //解析后的信息结构体，提供给已经解析好的信息的,选择使用
    private Object bean = null;

    public ReceiverMessage(byte[] bytes, int length){
        this.bytes = bytes;
    }

    public ReceiverMessage(Object bean){
        this.bean = bean;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Object getBean(){
        return bean;
    }

    @Override
    public String getMessage() {
        if(null != bytes) {
            return SerialPortUtils.ByteArrToHex(bytes);
        }else if(null != bean){
            return bean.toString();
        }

        return null;
    }
}
