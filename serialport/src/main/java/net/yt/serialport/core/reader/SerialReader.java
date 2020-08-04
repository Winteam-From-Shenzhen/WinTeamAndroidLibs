package net.yt.serialport.core.reader;

import net.yt.cmd.ReceiverMessage;
import net.yt.impl.OnDataReceive;
import net.yt.util.SerialLog;

import java.io.InputStream;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 10:33
 * Package name : net.yt.serialport.core.poll
 * Des :
 */
public abstract class SerialReader implements Runnable{

    protected volatile boolean mStopFlag = false;
    private InputStream mInputStream;
    private OnDataReceive onDataReceive;

    protected String path;
    /**
     *
     * @param path
     * @param onDataReceive
     */
    public SerialReader(String path ,OnDataReceive onDataReceive) {
        this.onDataReceive = onDataReceive;
        this.path = path;
        mStopFlag = false;
    }


    public void setInputStream(InputStream mInputStream) {
        this.mInputStream = mInputStream;
    }

    public OnDataReceive getOnDataReceive() {
        return onDataReceive;
    }

    public void start() {
        mStopFlag = false;
    }

    public void stop() {
        mStopFlag = true;
    }

    @Override
    public void run() {
        SerialLog.e("read " + Thread.currentThread().getId() + " thread running ...");

        while (!mStopFlag) {
            if (mInputStream == null) {
                SerialLog.e( "mInputStream is null!!!");
                continue;
            }

            if (!readHeader(mInputStream)) {
                SerialLog.e( "read header is false!!!");
                mStopFlag = true;
                continue;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SerialLog.e("read " + Thread.currentThread().getId() + " thread stop ...");
    }


    /**
     *
     * @param mInputStream
     * @return
     */
    protected abstract boolean readHeader(InputStream mInputStream);

    public abstract String getName();

    /**
     *
     * @param bytes
     * @param length
     */
    protected void postReceiver(byte[] bytes,int length){

        if (onDataReceive == null){
            return;
        }

        ReceiverMessage receiverMessage = new ReceiverMessage(bytes, length);
        onDataReceive.onDataReceiver(path,receiverMessage);
    }

    protected void postReceiver(Object bean){

        if (onDataReceive == null){
            return;
        }

        ReceiverMessage receiverMessage = new ReceiverMessage(bean);
        onDataReceive.onDataReceiver(path, receiverMessage);
    }

}
