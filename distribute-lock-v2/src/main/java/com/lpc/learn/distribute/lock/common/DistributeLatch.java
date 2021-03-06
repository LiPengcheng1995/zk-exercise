package com.lpc.learn.distribute.lock.common;

import com.lpc.learn.distribute.lock.zk.BlockNode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.common
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 20:11
 * Description: 用法有点像 {@link java.util.concurrent.CountDownLatch}，但不完全一样，线程进入阻塞
 * 等待分布式队列中的ok消息。
 */
public interface DistributeLatch {

    BlockNode addAndWait(Long time, TimeUnit unit) throws IOException;

    void free(BlockNode blockNode);
}
