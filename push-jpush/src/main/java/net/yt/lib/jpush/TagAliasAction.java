package net.yt.lib.jpush;

import net.yt.lib.push.IOperateCallback;

public class TagAliasAction implements Runnable {
    public TagAliasManager tagAliasManager;
    public int sequence;
    public IOperateCallback cb;

    public TagAliasAction(TagAliasManager manager, int sequence, IOperateCallback cb){
        this.tagAliasManager = manager;
        this.sequence = sequence;
        this.cb = cb;
    }

    @Override
    public void run() {
        if(tagAliasManager != null){
            tagAliasManager.timeoutAction(sequence);
            tagAliasManager = null;
        }
    }

}
