package net.yt.serialport.core.writer;

import androidx.annotation.NonNull;

import net.yt.cmd.SendMessage;
import net.yt.serialport.SerialPort;
import net.yt.serialport.SerialPortHelper;
import net.yt.util.SerialLog;

import java.io.IOException;

/**
 * Auth : xiao.yunfei
 * Date : 2019/10/11 20:38
 * Package name : net.yt.serialport.core.writer
 * Des :
 */
public class SerialWriter implements Runnable {
    private SerialPortHelper serialPortHelper;

    public SerialWriter(SerialPortHelper serialPortHelper) {
        this.serialPortHelper = serialPortHelper;
    }

    @Override
    public void run() {
        while (true) {
            SendMessage sendMessage = SendMessageQueue.getInstance().takeMessage();
            if (sendMessage != null){
                //如果收到结束的信息，则退出线程
                if(sendMessage.isStopFlag()){
                    break;
                }

                try {
                    sendCmdByPort(sendMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        //发送结束位消息退出线程
        SendMessage msg = new SendMessage(null, null);
        msg.setStopFlag(true);
        SendMessageQueue.getInstance().add(msg);
    }

    private void sendCmdByPort(@NonNull SendMessage sendMessage) throws IOException {

//        byte[] cmd = sendMessage.getBytes();
        String port = sendMessage.getPort();
        SerialPort serialPort = serialPortHelper.getDevice(port);
        if (serialPort == null){
            SerialLog.e("sendCmdByPort error : "+ port + " is closed");
            return;
        }
        serialPort.sendCommand(sendMessage);
    }
}
