package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeLatch;
import com.lpc.learn.distribute.lock.common.DistributeQueueNode;
import com.lpc.learn.distribute.lock.common.exception.DistributeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 20:12
 * Description:
 */
public class ZKDistributeLatch implements DistributeLatch, Watcher {

    public ConcurrentMap<String, BlockNode> waitMap = new ConcurrentHashMap<>();

    public ZKQueueClient client;


    public ZKDistributeLatch(String host,Integer sessionTimeOut,String basePrefixId) throws IOException {
        this.client =new ZKQueueClient(host,sessionTimeOut,basePrefixId);
    }

    @Override
    public BlockNode addAndWait(Long time, TimeUnit unit) throws IOException {
            DistributeQueueNode distributeNode = client.add();
            DistributeQueueNode distributeNodePre = client.getPre(distributeNode);
            if (distributeNodePre == null) {
                throw new DistributeException("刚加入的节点不见了");
            }


            BlockNode node = new BlockNode();
            node.setThread(Thread.currentThread());
            node.setWatchedNode(distributeNodePre);
            node.setThisNode(distributeNode);
            waitMap.putIfAbsent(distributeNodePre.getId(), node);
            if (distributeNodePre == client.head) {
                return node;
            }

            Long startTime = System.currentTimeMillis();
            Long endTime = startTime + unit.convert(time, TimeUnit.MILLISECONDS);
            while (System.currentTimeMillis() < endTime) {
                client.watchToBeDelete(distributeNodePre, this);
                if (node.ifSuccess) {
                    return node;
                }
            }
            if (!node.ifSuccess) {
                waitMap.remove(distributeNodePre.getId());
                client.delete(distributeNode);
                return null;
            }
            return node;
    }

    @Override
    public void free(BlockNode blockNode) {
        waitMap.remove(blockNode.getWatchedNode().getId());
        client.delete(blockNode.getThisNode());
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent == null) {
            return;
        }
        if (waitMap.get(watchedEvent.getPath()) != null) {
            if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                waitMap.get(watchedEvent.getPath()).setIfSuccess(true);
                waitMap.get(watchedEvent.getPath()).getThread().interrupt();
            } else {
                client.watchToBeDelete(waitMap.get(watchedEvent.getPath()).getWatchedNode(), this);
            }
        }
    }
}
