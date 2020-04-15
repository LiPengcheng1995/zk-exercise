package com.lpc.learn.distribute.lock;

/**
 * Package: com.lpc.learn.zk.distribute.lock
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 16:29
 * Description:
 */
public interface Synchronizer {
    /**
     * 同步器的前缀id，一般是业务相关的键值，比如下单、删除之类的
     * @return
     */
    String getBasePrefixId();
}
