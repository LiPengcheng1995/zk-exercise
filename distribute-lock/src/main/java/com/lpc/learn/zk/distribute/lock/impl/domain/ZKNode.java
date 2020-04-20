package com.lpc.learn.zk.distribute.lock.impl.domain;

import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeInput;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:16
 * Description:
 */
public class ZKNode implements Node {

    String baseId;

    String surfix;

    public ZKNode(String baseId, String surfix) {
        this.baseId = baseId;
        this.surfix = surfix;
    }

    @Override
    public String getId() {
        return baseId+SEPRATOR+surfix;
    }

    @Override
    public String getSurfix() {
        return surfix;
    }

    public void setSurfix(String surfix) {
        this.surfix = surfix;
    }

    @Override
    public String getBaseId() {
        return baseId;
    }
}
