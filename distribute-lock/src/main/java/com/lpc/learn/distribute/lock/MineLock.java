package com.lpc.learn.distribute.lock;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 16:57
 * Description:
 */
public interface MineLock {

    /**
     * 尝试获得锁
     * @return
     */
    boolean tryLock();

    /**
     * 阻塞等待获得锁
     * @param time
     * @param unit
     * @return
     */
    boolean lock(Long time, TimeUnit unit);

    /**
     * 释放锁
     * @return
     */
    boolean release();
}
