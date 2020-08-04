package net.yt.serialport.core.writer;

import net.yt.cmd.SendMessage;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Auth : xiao.yunfei
 * Date : 2019/10/11 21:05
 * Package name : net.yt.serialport.core.writer
 * Des :
 */
public class SendMessageQueue {
    private static SendMessageQueue sendMessageQueue;

    private LinkedBlockingDeque<SendMessage> messageLinkedBlockingDeque;
    public static SendMessageQueue getInstance(){
        if (sendMessageQueue == null){
            synchronized (SendMessageQueue.class) {
                if(sendMessageQueue == null) {
                    sendMessageQueue = new SendMessageQueue();
                }
            }
        }
        return sendMessageQueue;
    }
    private SendMessageQueue(){
        messageLinkedBlockingDeque = new LinkedBlockingDeque<>();
    }


    public void add(SendMessage sendMessage){
        messageLinkedBlockingDeque.offer(sendMessage);
    }

    SendMessage takeMessage(){
        return messageLinkedBlockingDeque.poll();
    }
}
