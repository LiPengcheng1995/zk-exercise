package com.lpc.lean.zk.service.impl;

import com.lpc.lean.zk.domain.ZKNodeEvent;
import com.lpc.lean.zk.service.ZKService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Package: com.lpc.lean.zk.service.impl
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/19
 * Time: 15:56
 * Description:
 */
@Slf4j
@Service("zkService")
public class ZKServiceImpl implements ZKService, InitializingBean {


    @Value("${zk.host}")
    private String host;

    @Value("${zk.sessionTimeOut}")
    private Integer sessionTimeOut;


    public ZooKeeper zooKeeper;

    private volatile CountDownLatch startLatch;

    private Map<String, Consumer<ZKNodeEvent>> listenerMap = new ConcurrentHashMap<>();


    @Override
    public void addWatch(String zkPath, Consumer<ZKNodeEvent> consumer) {
        listenerMap.put(zkPath, consumer);
        try {
            zooKeeper.exists(zkPath,this);
        } catch (Throwable throwable) {
            log.error("查询zk时出现异常,",throwable);
        }
    }

    @Override
    public void removeWatch(String zkPath) {
        listenerMap.remove(zkPath);
    }

    public void suspendUntilStartFinish() {
        try {
            startLatch = new CountDownLatch(1);
            startLatch.await();
            log.info("完成 zk 客户端创建，结束阻塞，sessionId:{},sessionPassWd:{}", zooKeeper.getSessionId(), zooKeeper.getSessionPasswd());
        } catch (InterruptedException e) {
            log.error("阻塞过程中抛出异常,", e);
            throw new RuntimeException("zk 创建阻塞过程中出现异常", e);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.None) {
            // 这个事件是连接状态改变的事件
            dealWithConnectStateChange(watchedEvent);
        } else {
            // 这个事件是节点状态改变的事件
            dealWithNodeStateChange(watchedEvent);
        }
    }

    private void dealWithNodeStateChange(WatchedEvent watchedEvent) {
        String path = watchedEvent.getPath();
        if (path == null) {
            log.info("没有变动路径，不处理");
            return;
        }
        Consumer<ZKNodeEvent> consumer = listenerMap.get(path);
        if (consumer == null) {
            log.info("没有注册次节点变动的监控，不处理");
            return;
        }
        try {
            String newValue = Arrays.toString(zooKeeper.getData(path, this, null));
            log.info("收到 ZK 的节点改变通知，path:{},newValue:{}", watchedEvent.getPath(), newValue);

            ZKNodeEvent event = new ZKNodeEvent();
            event.setPath(path);
            event.setNewValue(newValue);
            consumer.accept(event);

        } catch (Throwable throwable) {
            log.error("反查节点最新信息时并通知前端时出现异常，path:{},",path,throwable);
        }

    }

    private void dealWithConnectStateChange(WatchedEvent watchedEvent) {
        switch (watchedEvent.getState()) {
            case SyncConnected:
                // 完成连接
                if (startLatch != null) {
                    log.error("收到连接成功消息，并通知阻塞的线程");
                    startLatch.countDown();
                } else {
                    log.error("收到连接成功消息，但是没有阻塞等待的，不再通知");
                }
                break;
            case Expired:
                // 连接丢失，需要重新建立连接
                refreshZKClient();
                break;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshZKClient();
    }

    private void refreshZKClient() {
        try {
            log.info("开始创建 zk 客户端");
            zooKeeper = new ZooKeeper(host, sessionTimeOut, this);
            suspendUntilStartFinish();
            log.info("完成创建 zk 客户端");
        } catch (IOException e) {
            log.error("创建 ZK 客户端过程中收到异常，", e);
        }
    }
}
