package net.yt.lib.push;

public class Message {
    private int notifyId;  //这个字段用于通知的消息类型，在透传中都是默认0
    private String messageId;
    private String title;
    private String message;
    //额外消息（例如小米推送里面的传输数据）
    private String extra;
    private String target;

    public int getNotifyID() {
        return notifyId;
    }

    public void setNotifyID(int notifyID) {
        this.notifyId = notifyID;
    }

    public String getMessageID() {
        return messageId;
    }

    public void setMessageID(String messageID) {
        this.messageId = messageID;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Message{" +
                "notifyID=" + notifyId +
                ", messageID='" + messageId + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", extra='" + extra + '\'' +
                ", target=" + target +
                '}';
    }
}
