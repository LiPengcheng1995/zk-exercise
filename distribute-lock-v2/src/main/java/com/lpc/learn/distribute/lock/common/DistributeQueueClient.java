package com.lpc.learn.distribute.lock.common;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.common
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 19:41
 * Description: 封装的一个队列的客户端，里面是由 zk 或者其他的技术进行的底层实现
 */
public interface DistributeQueueClient {

    DistributeNode add();

    boolean delete(DistributeNode node);

    int getPosition();

    DistributeNode getPre();

    DistributeNode getAfter();

    boolean addAndWaitToBeHead(Long time, TimeUnit unit);

    interface DistributeNode {

    }
}
