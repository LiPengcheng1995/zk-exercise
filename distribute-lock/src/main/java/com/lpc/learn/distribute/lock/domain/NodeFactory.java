package com.lpc.learn.distribute.lock.domain;

/**
 * Package: com.lpc.learn.distribute.lock.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/20
 * Time: 19:39
 * Description:
 */
public interface NodeFactory {
    Node getFromString(String string);
}
