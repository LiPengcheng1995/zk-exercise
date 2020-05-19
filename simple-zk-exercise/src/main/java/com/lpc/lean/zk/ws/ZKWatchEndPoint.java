package com.lpc.lean.zk.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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
@ServerEndpoint("/webSocket/zk/{zkNodeId}")
public class ZKWatchEndPoint {

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("zkNodeId") String zkNodeId) throws IOException {
        String mess = "收到 WebSocket 建立请求，wsId:%s,zkNodeId:%s";
        mess = String.format(mess, session.getId(),zkNodeId);
        log.info(mess);
        sessionMap.put(zkNodeId, session);
        session.getBasicRemote().sendText(mess);
    }

    @OnClose
    public void onClose(Session session,@PathParam("zkNodeId") String zkNodeId) {
        log.info("收到 WebSocket 关闭请求，wsId:{},zkNodeId:{}", session.getId(),zkNodeId);
        sessionMap.remove(zkNodeId);
    }

    @OnMessage
    public void onMessage(Session session, String message,@PathParam("zkNodeId") String zkNodeId) throws IOException {
        String mess = "收到 WebSocket 消息，wsId:%s,zkNodeId:%s,message:%s";
        mess = String.format(mess, session.getId(),zkNodeId,message);
        log.info(mess);
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

}
