package com.lpc.learn.distribute.lock.zk;

import com.alibaba.fastjson.JSON;
import com.lpc.learn.distribute.lock.common.DistributeLatch;
import com.lpc.learn.distribute.lock.common.DistributeQueueClient;
import com.lpc.learn.distribute.lock.common.DistributeQueueNode;
import com.lpc.learn.distribute.lock.common.exception.DistributeException;
import com.lpc.learn.distribute.lock.common.exception.RepeatOperateException;
import com.lpc.learn.distribute.lock.common.exception.RetryException;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 20:01
 * Description:
 */
public class ZKQueueClient implements DistributeQueueClient {

    private ZooKeeper zooKeeper;

    private String basePrefixId;

    ZKDistributeQueueNodeComparator comparator = new ZKDistributeQueueNodeComparator();

    DistributeQueueNode head = null;

    DistributeQueueNode tail = null;

    public static List<ACL> acls = new ArrayList<>();

    static {
        acls.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE));
    }

    public ZKQueueClient(ZooKeeper zooKeeper, String basePrefixId) {
        this.zooKeeper = zooKeeper;
        this.basePrefixId = basePrefixId;
        this.head = new ZKDistributeQueueNode(basePrefixId, String.valueOf(zooKeeper.getSessionId()), "-1");
        this.tail = new ZKDistributeQueueNode(basePrefixId, String.valueOf(zooKeeper.getSessionId()), "-2");
    }

    @Override
    public DistributeQueueNode add() {

        DistributeQueueNode node = getLocalNode();
        if (node != null) {
            return node;
        }
        String result = "";
        try {
            result = zooKeeper.create(buildBasicNode().getId(), null, acls, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new RetryException("创建节点发生zk异常,id:" + buildBasicNode().getId(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RetryException("创建节点阻塞被打断,id:" + buildBasicNode().getId(), e);
        }

        System.out.println("插入队列，id:" + buildBasicNode().getId() + ",result:" + result);
        if (StringUtils.isBlank(result)) {
            throw new RetryException("创建节点失败，返回空,id:" + buildBasicNode().getId());
        }
        return new ZKDistributeQueueNode(result);
    }

    @Override
    public boolean delete(DistributeQueueNode node) {
        try {
            zooKeeper.delete(node.getId(), -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            throw new RetryException("节点删除失败");
        }
        return true;
    }


    private DistributeQueueNode getLocalNode() {
        List<DistributeQueueNode> currentList = getAllNodesSortedByCreateTime();
        if (currentList == null) {
            return null;
        }

        String localId = String.valueOf(zooKeeper.getSessionId());
        for (int i = 0; i < currentList.size(); i++) {
            if (localId.equals(currentList.get(i).getLocalId())) {
                return currentList.get(i);
            }
        }
        return null;
    }

    @Override
    public DistributeQueueNode getPre(DistributeQueueNode node) {
        List<DistributeQueueNode> currentList = getAllNodesSortedByCreateTime();
        if (currentList == null) {
            return null;
        }

        for (int i = 0; i < currentList.size(); i++) {
            if (node.getId().equals(currentList.get(i).getId())) {
                if (i > 0) {
                    return currentList.get(i - 1);
                } else {
                    return head;
                }
            }
        }
        return null;
    }

    @Override
    public DistributeQueueNode buildBasicNode() {
        ZKDistributeQueueNode node = new ZKDistributeQueueNode();
        node.setBaseId(this.basePrefixId);
        node.setLocalId(String.valueOf(zooKeeper.getSessionId()));
        return node;
    }

    @Override
    public boolean watchToBeDelete(DistributeQueueNode node, DistributeLatch latch) {
        ZKDistributeLatch zkDistributeLatch;
        if (latch instanceof ZKDistributeLatch){
            zkDistributeLatch = (ZKDistributeLatch) latch;
        }else {
            throw new DistributeException("入参不合理");
        }
        try {
            Stat stat = zooKeeper.exists(node.getId(), zkDistributeLatch);
            if (stat == null){
                return true;
            }
        } catch (KeeperException e) {
            System.out.println("阻塞时发生zk异常");
        } catch (InterruptedException e) {
            System.out.println("阻塞时被打断");
        }
        return false;
    }


    private List<DistributeQueueNode> getAllNodesSortedByCreateTime() {
        try {
            List<String> strings = zooKeeper.getChildren(basePrefixId, false);
            System.out.println(JSON.toJSONString(strings));
            if (strings == null) {
                System.out.println("从对应的锁节点下没有拿到子节点");
                return new ArrayList<>();
            }

            return strings
                    .stream()
                    .map(ZKDistributeQueueNode::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new RetryException("查询所有子节点发生zk异常", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RetryException("查询所有子节点被打断", e);
        }
    }

}
