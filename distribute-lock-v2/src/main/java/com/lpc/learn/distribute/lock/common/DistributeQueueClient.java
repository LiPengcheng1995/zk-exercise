package com.lpc.learn.distribute.lock.common;

/**
 * Package: com.lpc.learn.distribute.lock.common
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 19:41
 * Description: 封装的一个队列的客户端，里面是由 zk 或者其他的技术进行的底层实现
 */
public interface DistributeQueueClient {

    DistributeQueueNode add();

    boolean delete(DistributeQueueNode node);

    DistributeQueueNode getPre(DistributeQueueNode node);

    DistributeQueueNode buildBasicNode();

    boolean watchToBeDelete(DistributeQueueNode node, DistributeLatch latch);

}
