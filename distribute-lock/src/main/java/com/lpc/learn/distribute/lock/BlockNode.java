package com.lpc.learn.distribute.lock;

/**
 * Package: com.lpc.learn.distribute.lock
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 19:23
 * Description:
 */
public interface BlockNode {

    String getUniqueId();

    boolean getIfHead();

    void setIfHead(boolean ifHead);

    void setThread(Thread thread);

    Thread getThread();
}
