package com.lpc.learn.zk.distribute.lock.impl.domain;

import com.lpc.learn.distribute.lock.domain.NodeInput;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/14
 * Time: 17:16
 * Description:
 */
public class ZKNodeInput implements NodeInput {
    private String baseId;

    public ZKNodeInput(String baseId) {
        this.baseId = baseId+"-";
    }

    @Override
    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId+"-";
    }
}
