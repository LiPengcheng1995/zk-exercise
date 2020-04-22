package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeQueueNode;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 19:55
 * Description:
 */
public class BlockNode {

    DistributeQueueNode watchedNode;

    DistributeQueueNode thisNode;

    boolean ifSuccess;

    Thread thread;

    public DistributeQueueNode getWatchedNode() {
        return watchedNode;
    }

    public void setWatchedNode(DistributeQueueNode watchedNode) {
        this.watchedNode = watchedNode;
    }

    public boolean isIfSuccess() {
        return ifSuccess;
    }

    public void setIfSuccess(boolean ifSuccess) {
        this.ifSuccess = ifSuccess;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public DistributeQueueNode getThisNode() {
        return thisNode;
    }

    public void setThisNode(DistributeQueueNode thisNode) {
        this.thisNode = thisNode;
    }
}
