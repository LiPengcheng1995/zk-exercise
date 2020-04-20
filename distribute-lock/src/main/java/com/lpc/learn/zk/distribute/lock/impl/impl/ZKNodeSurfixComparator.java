package com.lpc.learn.zk.distribute.lock.impl.impl;

import com.lpc.learn.zk.distribute.lock.impl.domain.ZKNode;

import java.util.Comparator;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl.impl
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/20
 * Time: 20:49
 * Description:
 */
public class ZKNodeSurfixComparator implements Comparator<ZKNode> {
    @Override
    public int compare(ZKNode o1, ZKNode o2) {
        int o1Surfix = Integer.parseInt(o1.getSurfix());
        int o2Surfix = Integer.parseInt(o2.getSurfix());
        return o1Surfix-o2Surfix;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ZKNodeSurfixComparator;
    }
}
