package com.lpc.learn.zk.distribute.lock.impl;

import com.alibaba.fastjson.JSON;
import com.lpc.learn.distribute.lock.SybchronizedQueue;
import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;
import com.lpc.learn.exception.RepeatOperateException;
import com.lpc.learn.exception.RetryException;
import com.lpc.learn.zk.distribute.lock.impl.domain.ZKNodeFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:13
 * Description:
 */
public class ZooKeeperSynchronizedQueue implements SybchronizedQueue {
    private String basePrefixId;
    private ZooKeeper zooKeeper;
    private ZooKeeperWatcher watcher;
    public static final String SEPRATOR = "/";
    public static List<ACL> acls = new ArrayList<>();
    public static ZKNodeFactory zkNodeFactory = new ZKNodeFactory();

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
        return zkNodeFactory.getFromString(result);
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
                        return zkNodeFactory.getFromString(s);
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
        try {
            List<String> strings = zooKeeper.getChildren(getBasePrefixId(), false);
            System.out.println(JSON.toJSONString(strings));
            if (strings == null) {
                System.out.println("从对应的锁节点下没有拿到子节点，node:" + JSON.toJSONString(node));
                return false;
            }
            int current = Integer.parseInt(node.getSurfix());
            for (String s : strings) {
                int temp = Integer.parseInt(s.split(NodeInput.SEPRATOR)[1]);
                if (temp < current) {
                    System.out.println("拿到一个比当前节点还小的下表，s:" + s);
                    return false;
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getBasePrefixId() {
        return basePrefixId;
    }
}
