package net.yt.serialport;

import android.text.TextUtils;

import net.yt.cmd.ReceiverMessage;
import net.yt.cmd.SendMessage;
import net.yt.impl.CommonReader;
import net.yt.impl.OnDataReceive;
import net.yt.serialport.core.poll.ThreadPool;
import net.yt.serialport.core.reader.SerialReader;
import net.yt.serialport.core.writer.SendMessageQueue;
import net.yt.serialport.core.writer.SerialWriter;
import net.yt.util.SerialLog;
import net.yt.util.SerialPortUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SerialPortHelper {

    private static SerialPortHelper serialPortHelper;
    private SerialPortFinder portFinder;
    private final List<SerialPort> serialPorts = new ArrayList<>();
    private SerialWriter mSerialWriter;

    public static SerialPortHelper getSerialPortHelper() {
        if (serialPortHelper == null) {
            synchronized (SerialPortHelper.class) {
                if (serialPortHelper == null) {
                    serialPortHelper = new SerialPortHelper();
                }
            }
        }
        return serialPortHelper;
    }

    private SerialPortHelper() {
        SerialLog.init();
        portFinder = new SerialPortFinder();
        mSerialWriter = new SerialWriter(this);
        ThreadPool.getThreadPool().addTask(mSerialWriter);
    }


    //打开串口，串口路径，帧率，读取线程
    public boolean openDevice(String sPort, int iBaudRate, SerialReader serialReader) {

        String[] devices = portFinder.getAllDevices();

        if (devices == null || devices.length == 0) {
            return false;
        }

        if (serialReader == null) {
            serialReader = new CommonReader(sPort, defaultDataReceive);
        }

        synchronized (serialPorts) {

            if (hasDevice(sPort)) {
                return false;
            }

            SerialPort serialPort = new SerialPort(sPort, iBaudRate);

            if (serialPort.openDevice()) {
                serialPort.startReadTread(serialReader);


                if (!hasDevice(sPort)) {
                    serialPorts.add(serialPort);
                }

            }

        }

        return true;
    }


    private boolean hasDevice(String sport) {
        for (SerialPort serialPort : serialPorts) {
            if (TextUtils.equals(sport, serialPort.getPortPath())) {
                return true;
            }
        }
        return false;
    }


    public SerialPort getDevice(String sPort) {
        for (SerialPort serialPort : serialPorts) {
            if (TextUtils.equals(sPort, serialPort.getPortPath())) {
                return serialPort;
            }
        }
        return null;
    }


    public void close(String port) {

        SerialPort serialPort = getDevice(port);
        if (serialPort != null) {
            serialPort.closeDevice();
            serialPort = null;
        }

        //关闭写线程
        mSerialWriter.stop();

    }

    public void sendHex(String sHex, String port) {
        byte[] bOutArray = SerialPortUtils.HexToByteArr(sHex);
        send(bOutArray, port);
    }

    public void sendSyncHex(String sHex, String port) {
        byte[] bOutArray = SerialPortUtils.HexToByteArr(sHex);
        sendSyncMessage(bOutArray, port);
    }

    /**
     * @param bOutArray
     * @param port
     */
    private  void send(byte[] bOutArray, String port) {
        SerialPort serialPort = getDevice(port);
        if (serialPort == null) {
            return;
        }
        try {
            serialPort.sendCommand(port,bOutArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bOutArray
     * @param port
     */
    private void sendSyncMessage(byte[] bOutArray, String port) {
        SendMessage sendMessage = new SendMessage(port, bOutArray);
        SendMessageQueue.getInstance().add(sendMessage);
    }



    //默认的接收器
    private OnDataReceive defaultDataReceive = new OnDataReceive() {

        @Override
        public void onDataReceiver(String path, ReceiverMessage receiverMessage) {
        }

        @Override
        public void onSendCommand(SendMessage sendMessage) {
        }
    };

}
