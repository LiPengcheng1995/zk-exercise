package com.lpc.learn.distribute.lock.domain;

/**
 * Package: com.lpc.learn.distribute.lock.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 16:51
 * Description:
 */
public interface NodeInput {
    String SEPRATOR = "-";

    /**
     * 最后一级路径前缀
     * @return
     */
    String getBaseId();
}
