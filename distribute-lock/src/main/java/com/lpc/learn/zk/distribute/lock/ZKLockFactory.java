package com.lpc.learn.zk.distribute.lock;

import com.lpc.learn.zk.distribute.lock.impl.ZooKeeperLock;
import com.lpc.learn.zk.distribute.lock.impl.ZooKeeperSynchronizer;
import com.lpc.learn.zk.distribute.lock.impl.ZooKeeperWatcher;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

/**
 * Package: com.lpc.learn.zk.distribute.lock
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:21
 * Description:
 */
public class ZKLockFactory {
    private static ZooKeeperWatcher zooKeeperWatcher = new ZooKeeperWatcher();
    public static ZooKeeperLock build(ZooKeeper zooKeeper,String lockKey) throws KeeperException, InterruptedException {
        ZooKeeperSynchronizer synchronizer = new ZooKeeperSynchronizer(lockKey,zooKeeper,zooKeeperWatcher);
        return new ZooKeeperLock(synchronizer);
    }
}
