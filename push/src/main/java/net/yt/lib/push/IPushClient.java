package net.yt.lib.push;

import android.content.Context;

public interface IPushClient {

    /**
     * 初始化
     * @param context
     */
    void init(Context context, boolean isDebug, IPushReceiver receiver);

    /**
     * 设置别名，需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     * 每个用户只能指定一个别名。
     */
    void setAlias(String alias, IOperateCallback cb);

    /**
     * 清除别名
     */
    void clearAlias(IOperateCallback cb);

    /**
     * 设置标签，需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     * 标签之间用英文逗号隔开
     */
    void setTags(String tags, IOperateCallback cb);

    /**
     * 清除标签
     */
    void clearTags(IOperateCallback cb);

}
