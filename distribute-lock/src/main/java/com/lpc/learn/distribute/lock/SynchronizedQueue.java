package com.lpc.learn.distribute.lock;

import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 16:46
 * Description: 一个同步队列的维护 api
 */
public interface SynchronizedQueue extends BlockNode{

    /**
     * 向同步器中加入等待节点
     *
     * @param input
     * @return
     */
    Node add(NodeInput input);

    /**
     * 入队并阻塞等待排到队头
     * @param input
     * @return true 排到了
     *         false 时间到了
     */
    boolean addAndWaitToBeHead(NodeInput input,Long time, TimeUnit unit);

    /**
     * 从同步器中删除等待节点
     *
     * @return
     */
    boolean del(Node node);


    Node getExistNode(NodeInput input);

    Node getPre(NodeInput input);

    Node getPost(NodeInput input);


}