package com.lpc.learn.zk.distribute.lock.impl;

import com.alibaba.fastjson.JSON;
import com.lpc.learn.distribute.lock.SynchronizedQueue;
import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;
import com.lpc.learn.exception.RepeatOperateException;
import com.lpc.learn.exception.RetryException;
import com.lpc.learn.zk.distribute.lock.impl.domain.ZKNodeFactory;
import com.lpc.learn.zk.distribute.lock.impl.impl.ZKNodeSurfixComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:13
 * Description:
 */
public class ZooKeeperSynchronizedQueue implements SynchronizedQueue {
    private String basePrefixId;
    private ZooKeeper zooKeeper;
    private ZooKeeperWatcher watcher;
    public static final String SEPRATOR = "/";
    public static List<ACL> acls = new ArrayList<>();
    public static ZKNodeFactory factory = new ZKNodeFactory();
    public static ZKNodeSurfixComparator comparator = new ZKNodeSurfixComparator();

    static {
        acls.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE));
    }

    public ZooKeeperSynchronizedQueue(String basePrefixId, ZooKeeper zooKeeper, ZooKeeperWatcher zooKeeperWatcher) throws KeeperException, InterruptedException {
        this.basePrefixId = basePrefixId;
        this.zooKeeper = zooKeeper;
        this.watcher = zooKeeperWatcher;
        Stat stat = zooKeeper.exists(basePrefixId, watcher);
        //todo 这里不知道不存在返回什么
        if (stat == null) {
            String path = zooKeeper.create(basePrefixId, null, acls, CreateMode.PERSISTENT);
            System.out.println("创建锁的节点完成，path:" + path);
        }
    }

    @Override
    public Node add(NodeInput input) {
        Node existNode = getExistNode(input);
        if (existNode != null) {
            // TODO 先不允许重复拿，后面再优化
            throw new RepeatOperateException("本机已拿到 zk 锁，不可重复获取");
        }

        String id = getBasePrefixId() + SEPRATOR + input.getBaseId();
        String result = "";
        try {
            result = zooKeeper.create(id, null, acls, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new RetryException("创建节点发生zk异常,input:" + JSON.toJSONString(input), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RetryException("创建节点阻塞被打断,input:" + JSON.toJSONString(input), e);
        }

        System.out.println("插入队列，input:" + JSON.toJSONString(input) + ",result:" + result);
        if (StringUtils.isBlank(result)) {
            throw new RetryException("创建节点失败，返回空,input:" + JSON.toJSONString(input));
        }
        return factory.getFromString(result);
    }

    @Override
    public boolean addAndWaitToBeHead(NodeInput input, Long time, TimeUnit unit) {

        return false;
    }


    @Override
    public boolean del(Node node) {
        Node existNode = getExistNode(node);
        try {
            zooKeeper.delete(node.getId(), -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean ifExistes(Node node) {
        try {
            Stat stat = zooKeeper.exists(node.getId(), false);
            if (stat == null) {
                return false;
            }
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Node getExistNode(NodeInput input) {
        List<Node> currentNodes = getAllNodesAndOrderBySurfix();
        if (currentNodes==null||currentNodes.isEmpty()){
            return null;
        }

        Node inputNode = null;
        if (input instanceof Node) {
            inputNode = (Node) input;
        }


        for (Node temp:currentNodes){
            if (inputNode != null){
                if (inputNode.equals())
            }
        }
        try {
            List<String> strings = zooKeeper.getChildren(getBasePrefixId(), false);
            System.out.println(JSON.toJSONString(strings));
            if (strings == null) {
                return null;
            }
            Node inputNode = null;
            if (input instanceof Node) {
                inputNode = (Node) input;
            }
            for (String s : strings) {
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                if (inputNode != null) {
                    if (s.equals(inputNode.getId())) {
                        return inputNode;
                    }
                } else {
                    if (s.contains(input.getBaseId())) {
                        return factory.getFromString(s);
                    }
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new RetryException("创建节点发生zk异常,input:" + JSON.toJSONString(input), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RetryException("创建节点阻塞被打断,input:" + JSON.toJSONString(input), e);
        }

        return null;
    }

    @Override
    public boolean ifHead(Node node) {
        List<Node> currentNodes = getAllNodesAndOrderBySurfix();
        if (currentNodes==null||currentNodes.isEmpty()){
            return false;
        }
        Node head = currentNodes.get(0);
        if (node.equals(head)){
            return true;
        }
        return false;
    }

    @Override
    public String getUniqueId(){
        return String.valueOf(zooKeeper.getSessionId());
    }

    private String getBasePrefixId() {
        return basePrefixId;
    }

    private List<Node> getAllNodesAndOrderBySurfix(){
        try {
            List<String> strings = zooKeeper.getChildren(getBasePrefixId(), false);
            System.out.println(JSON.toJSONString(strings));
            if (strings == null) {
                System.out.println("从对应的锁节点下没有拿到子节点");
                return new ArrayList<>();
            }

            return strings
                    .stream()
                    .map(factory::getFromString)
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
