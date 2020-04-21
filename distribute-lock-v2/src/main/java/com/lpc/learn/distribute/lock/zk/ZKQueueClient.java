package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeQueueClient;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public static final String SEPRATOR = "/";

    public static List<ACL> acls = new ArrayList<>();
    static {
        acls.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE));
    }

    public ZKQueueClient(ZooKeeper zooKeeper, String basePrefixId) {
        this.zooKeeper = zooKeeper;
        this.basePrefixId = basePrefixId;
    }

    @Override
    public DistributeNode add() {
        return null;
    }

    @Override
    public boolean delete(DistributeNode node) {
        return false;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public DistributeNode getPre() {
        return null;
    }

    @Override
    public DistributeNode getAfter() {
        return null;
    }

    @Override
    public boolean addAndWaitToBeHead(Long time, TimeUnit unit) {
        return false;
    }

}
