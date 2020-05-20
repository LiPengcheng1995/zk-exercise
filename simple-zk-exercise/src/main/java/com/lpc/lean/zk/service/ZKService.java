package com.lpc.lean.zk.service;

import com.lpc.lean.zk.domain.ZKNodeEvent;
import org.apache.zookeeper.Watcher;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Package: com.lpc.lean.zk.service
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/19
 * Time: 15:56
 * Description:
 */
public interface ZKService extends Watcher {
    void addWatch(String zkPath, Function<ZKNodeEvent,Boolean> consumer);
    void removeWatch(String zkPath);
}
