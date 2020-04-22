package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeLock;
import com.lpc.learn.distribute.lock.common.DistributeQueueNode;
import com.lpc.learn.distribute.lock.common.exception.DistributeException;
import com.lpc.learn.distribute.lock.common.exception.RepeatOperateException;
import org.apache.zookeeper.ZooKeeper;

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

    ZKDistributeLatch latch;

    ZKQueueClient client;

    public static ThreadLocal<BlockNode> nodeThreadLocal = new ThreadLocal<>();

    public ZKLock(ZooKeeper zooKeeper,String basePrefixId) {
        client = new ZKQueueClient(zooKeeper,basePrefixId);
        latch = new ZKDistributeLatch(client);
    }

    @Override
    public boolean lock(Long time, TimeUnit unit) {
        BlockNode node = latch.addAndWait(time,unit);
        if (node ==null){
            return false;
        }

        nodeThreadLocal.set(node);
        return true;
    }

    @Override
    public void release() {
        if (nodeThreadLocal.get() == null){
            throw new RepeatOperateException("无需释放或者已经释放");
        }
        latch.free(nodeThreadLocal.get());
        nodeThreadLocal.remove();
    }
}
