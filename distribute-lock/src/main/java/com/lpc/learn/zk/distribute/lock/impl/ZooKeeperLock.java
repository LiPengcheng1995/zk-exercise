package com.lpc.learn.zk.distribute.lock.impl;

import com.lpc.learn.distribute.lock.MineLock;
import com.lpc.learn.distribute.lock.SynchronizedQueue;
import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;
import com.lpc.learn.zk.distribute.lock.impl.domain.ZKNodeInput;

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

    private SynchronizedQueue synchronizedQueue;

    private static volatile Node zkNode = null;

    private static Object lock = new Object();

    public ZooKeeperLock(SynchronizedQueue synchronizedQueue) {
        this.synchronizedQueue = synchronizedQueue;
    }

    @Override
    public boolean tryLock() {
        if (zkNode != null){
            return true;
        }
        synchronized (lock){
            String uniqueId = synchronizedQueue.getUniqueId();
            NodeInput input = new ZKNodeInput(uniqueId);
            zkNode = synchronizedQueue.add(input);
            if (zkNode == null){
                System.out.println("获取锁失败！返回空");
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean lock(Long time, TimeUnit unit) {
        if (tryLock()){
            return true;
        }

        return false;
    }

    @Override
    public boolean release() {
        return false;
    }
}
