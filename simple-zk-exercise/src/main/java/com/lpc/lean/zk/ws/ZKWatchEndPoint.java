package com.lpc.lean.zk.ws;

import com.alibaba.fastjson.JSON;
import com.lpc.lean.zk.common.ApplicationContextUtil;
import com.lpc.lean.zk.domain.ZKNodeEvent;
import com.lpc.lean.zk.service.ZKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Package: com.lpc.lean.zk.ws
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/18
 * Time: 17:51
 * Description:
 */
@Slf4j
@Component
@ServerEndpoint("/webSocket/zk")
public class ZKWatchEndPoint{

    // key 为 session id
    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    // key 为 zk path ，value 为监控这些 path 的 session id
    public static Map<String, Set<String>> zkPathWatchMap = new ConcurrentHashMap<>();

    private ZKService zkService = ApplicationContextUtil.getBean("zkService");

    public static final String ADD = "add";
    public static final String REMOVE = "remove";

    @OnOpen
    public void onOpen(Session session) throws IOException {
        String mess = "收到 WebSocket 建立请求，wsId:%s";
        mess = String.format(mess, session.getId());
        log.info(mess);
        sessionMap.put(session.getId(), session);
        session.getBasicRemote().sendText(mess);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("收到 WebSocket 关闭请求，wsId:{}", session.getId());
        removeAllSessionId(session.getId());
        sessionMap.remove(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String mess = "收到 WebSocket 消息，wsId:%s,message:%s";
        mess = String.format(mess, session.getId(), message);
        log.info(mess);

        List<String> dataList = Arrays.asList(message.split(","));
        if (ADD.equals(dataList.get(0).trim())) {
            dataList.subList(1, dataList.size()).forEach((data) -> {
                Set<String> watcherSet = zkPathWatchMap.computeIfAbsent(data, k -> new HashSet<>());
                watcherSet.add(session.getId());
                zkService.addWatch(data, this::noticeTheFrontEnd);
            });
        } else {
            dataList.subList(1, dataList.size()).forEach((data) -> {
                Set<String> watcherSet = zkPathWatchMap.computeIfAbsent(data, k -> new HashSet<>());
                watcherSet.remove(session.getId());
            });
        }

        session.getBasicRemote().sendText(mess);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String mess = "处理 WebSocket 消息出现异常，wsId:%s,%s,";
        mess = String.format(mess, session.getId(), throwable.getMessage());
        log.info(mess, throwable);
        try {
            session.getBasicRemote().sendText(mess);
        } catch (IOException e) {
            log.info("", e);
        }
    }

    private void removeAllSessionId(String sessionId){
        zkPathWatchMap.values().forEach((set)->set.remove(sessionId));
    }

    private boolean noticeTheFrontEnd(ZKNodeEvent event) {
        String eventStr = JSON.toJSONString(event);
        Session session = sessionMap.get(event.getPath());
        if (session == null) {
            log.error("要发送的消息没有找到对应的session,event:{}", eventStr);
            zkService.removeWatch(event.getPath());
            return false;
        }

        log.info("找到消息发送用的 session,event:{},sessionId:{}", eventStr, session.getId());
        try {
            session.getBasicRemote().sendText(eventStr);
        } catch (IOException e) {
            log.error("向前端发送信息失败,event:{},sessionId:{}", eventStr, session.getId());
        }
        return true;
    }
}
