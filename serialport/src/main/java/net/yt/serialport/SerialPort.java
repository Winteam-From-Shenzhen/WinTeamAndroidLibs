package net.yt.serialport;

import android.text.TextUtils;

import net.yt.cmd.SendMessage;
import net.yt.serialport.core.reader.SerialReader;
import net.yt.util.SerialLog;
import net.yt.serialport.core.poll.ThreadPool;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * SerialPort  打开 ，关闭串口，开启读取 数据的 线程
 */
public class SerialPort {

    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    private String portPath;
    private File device;
    private int bitRate;

    private SerialReader serialReader;

    SerialPort(String portPath, int bitRate) {
        this.portPath = portPath;
        this.bitRate = bitRate;
    }


    String getPortPath() {
        return portPath;
    }


    /**
     * 开启串口
     *
     * @return
     */
    boolean openDevice() {
        if (TextUtils.isEmpty(portPath)) {

            return false;
        }

        if (device == null) {
            device = new File(portPath);
        }

        SerialLog.d("device => canRead: " + device.canRead() + " canWrite: " + device.canWrite());
        if (!device.canRead() || !device.canWrite()) {

            SerialLog.e("device  没有 读写权限");

            return false;
        }

        int flags = 0;
        FileDescriptor mFd = open(device.getAbsolutePath(), bitRate, flags);
        if (mFd == null) {
            SerialLog.e("native open returns null");

            return false;
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        return true;
    }

    /**
     * 关闭串口
     */
    void closeDevice() {

        try {
            serialReader.stop();
            if (mFileOutputStream != null) {
                mFileOutputStream.flush();
                mFileOutputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mFileOutputStream = null;
        }
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mFileInputStream = null;
        }
        close();


    }


    public void sendCommand(SendMessage sendMessage) throws IOException {

        if (sendMessage == null){
            SerialLog.e("sendMessage  is null ");
            return;
        }
        byte[] data = sendMessage.getBytes();
        if (data == null || data.length == 0){
            SerialLog.e("data byte is null ");
            return;
        }
        boolean sendResult = false;
        if (mFileOutputStream != null) {
            sendResult = true;
            mFileOutputStream.write(sendMessage.getBytes());
        }
        sendMessage.setSend(sendResult);
        if (serialReader.getOnDataReceive() != null) {
            serialReader.getOnDataReceive().onSendCommand(sendMessage);
        }
    }

    /**
     * 下发数据
     *
     * @param command
     * @throws IOException
     */
    void sendCommand(String port, byte[] command) throws IOException {
        SendMessage sendMessage = new SendMessage(port, command);

        if (mFileOutputStream == null) {
            if (serialReader.getOnDataReceive() != null) {
                sendMessage.setSend(false);
                serialReader.getOnDataReceive().onSendCommand(sendMessage);
            }
            return;
        }

        mFileOutputStream.write(command);
        if (serialReader.getOnDataReceive() != null) {
            serialReader.getOnDataReceive().onSendCommand(sendMessage);
        }
    }

    /**
     * 开启读数据的线程
     *
     * @param serialReader
     */
    void startReadTread(SerialReader serialReader) {

        this.serialReader = serialReader;
        serialReader.setInputStream(mFileInputStream);
        ThreadPool.getThreadPool()
                .addTask(serialReader);
    }


    //═════════════════════════════════════════════════════════════════════════════════════════════
    //═══════════════════════════════  Native Methods And Load Libs ═══════════════════════════════
    //═════════════════════════════════════════════════════════════════════════════════════════════
    static {
        System.loadLibrary("serialport");
    }


    /**
     * @param path
     * @param baudrate
     * @param flags
     * @return
     */
    private native FileDescriptor open(String path, int baudrate, int flags);

    /**
     *
     */
    private native void close();

}
