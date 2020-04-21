package com.lpc.learn.zk.distribute.lock.impl;

import com.alibaba.fastjson.JSON;
import com.lpc.learn.distribute.lock.SynchronizedQueue;
import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;
import com.lpc.learn.exception.InvalidParamException;
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


    public volatile boolean ifHead=false;
    public Thread thread;

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
            throw new RepeatOperateException("队列中已有可用节点");
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
        while (true){
            Node pre = this.getPre(input);
            try {
                Stat stat =  zooKeeper.exists(pre.getId(),watcher);
                if (stat == null){
                    return true;
                }
            } catch (KeeperException e) {
                e.printStackTrace();
                throw new RetryException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RetryException(e);
            }

            watcher.wait(this,time,unit);
            return ifHead;
        }

    }


    @Override
    public boolean del(Node node) {
        Node existNode = getExistNode(node);
        if (existNode == null){
            return false;
        }
        try {
            zooKeeper.delete(node.getId(), -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            throw new RetryException("节点删除失败");
        }
        return true;
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
                if (inputNode.equals(temp)){
                    return temp;
                }
            }else {
                if (input.getBaseId().equals(temp.getBaseId())){
                    return temp;
                }
            }
        }

        return null;
    }

    @Override
    public Node getPre(NodeInput input) {
        List<Node> currentList =  getAllNodesAndOrderBySurfix();
        Node inputNode = null;
        if (input instanceof Node) {
            inputNode = (Node) input;
        }

        for (int i=0;i<currentList.size();i++){
            Node temp = currentList.get(i);
            if (inputNode != null){
                if (inputNode.equals(temp)){
                    return i<=0?null:currentList.get(i-1);
                }
            }else {
                if (input.getBaseId().equals(temp.getBaseId())){
                    return i<=0?null:currentList.get(i-1);
                }
            }
        }
        throw new InvalidParamException("传入的节点在队列中不存在");
    }

    @Override
    public Node getPost(NodeInput input) {
        List<Node> currentList =  getAllNodesAndOrderBySurfix();
        Node inputNode = null;
        if (input instanceof Node) {
            inputNode = (Node) input;
        }

        for (int i=0;i<currentList.size();i++){
            Node temp = currentList.get(i);
            if (inputNode != null){
                if (inputNode.equals(temp)){
                    return i>=currentList.size()-1?null:currentList.get(i+1);
                }
            }else {
                if (input.getBaseId().equals(temp.getBaseId())){
                    return i>=currentList.size()-1?null:currentList.get(i+1);
                }
            }
        }
        throw new InvalidParamException("传入的节点在队列中不存在");
    }

    @Override
    public String getUniqueId(){
        return String.valueOf(zooKeeper.getSessionId());
    }

    @Override
    public boolean getIfHead() {
        return ifHead;
    }

    @Override
    public void setIfHead(boolean ifHead) {
        this.ifHead = ifHead;
    }

    @Override
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public Thread getThread() {
        return thread;
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
