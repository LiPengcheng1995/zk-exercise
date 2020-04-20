package com.lpc.learn.distribute.lock;

import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;

/**
 * Package: com.lpc.learn.distribute.lock
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 16:46
 * Description: 一个同步队列的维护 api
 */
public interface SybchronizedQueue {

    /**
     * 向同步器中加入等待节点
     *
     * @param input
     * @return
     */
    Node add(NodeInput input);

    /**
     * 从同步器中删除等待节点
     *
     * @return
     */
    boolean del(Node node);

    /**
     * 节点是否在同步器中存在
     *
     * @param node
     * @return
     */
    boolean ifExistes(Node node);

    Node getExistNode(NodeInput input);

    /**
     * 节点是否位于队首
     *
     * @param node
     * @return
     */
    boolean ifHead(Node node);
}
