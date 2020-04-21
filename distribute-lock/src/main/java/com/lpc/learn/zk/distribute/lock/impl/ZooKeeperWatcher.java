package com.lpc.learn.zk.distribute.lock.impl;

import com.lpc.learn.distribute.lock.BlockNode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:30
 * Description:
 */
public class ZooKeeperWatcher implements Watcher {
    ConcurrentLinkedDeque<BlockNode> blockQueue = new ConcurrentLinkedDeque<>();
    @Override
    public void process(WatchedEvent watchedEvent) {
    }

    public boolean wait(BlockNode node, Long time, TimeUnit unit) {
        node.setThread(Thread.currentThread());
        blockQueue.add(node);

        return false;
    }
}
