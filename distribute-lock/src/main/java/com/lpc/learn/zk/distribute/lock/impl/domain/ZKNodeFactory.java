package com.lpc.learn.zk.distribute.lock.impl.domain;

import com.lpc.learn.distribute.lock.domain.Node;
import com.lpc.learn.distribute.lock.domain.NodeFactory;

/**
 * Package: com.lpc.learn.zk.distribute.lock.impl.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/20
 * Time: 19:40
 * Description:
 */
public class ZKNodeFactory implements NodeFactory {
    @Override
    public ZKNode getFromString(String string) {
        String[] array = string.split(Node.SEPRATOR);
        return new ZKNode(array[0],array[1]);
    }
}
