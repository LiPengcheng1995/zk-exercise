package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeLock;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 19:45
 * Description:
 */
public class ZKLock implements DistributeLock {


    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean lock(Long time, TimeUnit unit) {
        return false;
    }

    @Override
    public boolean release() {
        return false;
    }
}
