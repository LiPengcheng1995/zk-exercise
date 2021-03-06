package com.lpc.learn.distribute.lock.domain;

/**
 * Package: com.lpc.learn.distribute.lock.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 16:49
 * Description:
 */
public interface Node extends NodeInput {

    /**
     * 全路径
     * @return
     */
    String getId();

    String getSurfix();
}
