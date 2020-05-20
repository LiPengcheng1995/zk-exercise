package com.lpc.lean.zk.ws;

import com.alibaba.fastjson.JSON;
import com.lpc.lean.zk.domain.ZKNodeEvent;
import com.lpc.lean.zk.service.ZKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CookieValue;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class ZKWatchEndPoint {

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    public static Map<String, List<String>> zkPathWatchMap = new ConcurrentHashMap<>();

    @Resource
    private ZKService zkService;

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
        sessionMap.remove(session.getId());
        List<String> pathList = zkPathWatchMap.get(session.getId());
        zkPathWatchMap.remove(session.getId());
        if (!CollectionUtils.isEmpty(pathList)){
            pathList.forEach(zkService::removeWatch);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String mess = "收到 WebSocket 消息，wsId:%s,message:%s";
        mess = String.format(mess, session.getId(),message);
        log.info(mess);

        List<String> dataList = Arrays.asList(message.split(","));
        if (ADD.equals(dataList.get(0).trim())){
            dataList.subList(1,dataList.size()).forEach((data)->zkService.addWatch(data,this::noticeTheFrontEnd));
        }else {
            dataList.subList(1,dataList.size()).forEach((data)->zkService.removeWatch(data));
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

    private void noticeTheFrontEnd(ZKNodeEvent event){
        String eventStr = JSON.toJSONString(event);
        Session session = sessionMap.get(event.getPath());
        if (session == null){
            log.error("要发送的消息没有找到对应的session,event:{}",eventStr);
            zkService.removeWatch(event.getPath());
            return;
        }

        log.info("找到消息发送用的 session,event:{},sessionId:{}",eventStr,session.getId());
        try {
            session.getBasicRemote().sendText(eventStr);
        } catch (IOException e) {
            log.error("向前端发送信息失败,event:{},sessionId:{}",eventStr,session.getId());
        }
    }

}
