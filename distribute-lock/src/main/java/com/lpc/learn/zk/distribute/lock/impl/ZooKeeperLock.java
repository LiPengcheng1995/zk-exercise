package com.lpc.learn.zk.distribute.lock.impl;

import com.lpc.learn.distribute.lock.MineLock;
import com.lpc.learn.distribute.lock.SybchronizedQueue;
import com.lpc.learn.distribute.lock.domain.Node;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:12
 * Description:
 */
public class ZooKeeperLock implements MineLock {

    private SybchronizedQueue synchronizer;

    public static ThreadLocal<Node>

    public ZooKeeperLock(SybchronizedQueue synchronizer) {
        this.synchronizer = synchronizer;
    }

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