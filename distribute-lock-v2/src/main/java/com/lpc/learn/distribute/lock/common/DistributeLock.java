package com.lpc.learn.distribute.lock.common;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.common
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 19:45
 * Description:
 */
public interface DistributeLock {
    boolean lock(Long time, TimeUnit unit);

    void release();

}
