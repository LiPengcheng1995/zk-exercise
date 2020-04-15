package com.lpc.learn.zk.distribute.lock.impl.domain;

import com.lpc.learn.distribute.lock.domain.Node;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:16
 * Description:
 */
public class ZKNode implements Node {
    String id;

    public ZKNode(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
