package com.lpc.lean.zk.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
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
@ServerEndpoint("/webSocket/zk/{id}")
public class ZKWatchEndPoint {

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathVariable("id") String id) throws IOException {
        String mess = "收到 WebSocket 建立请求，wsId:%s";
        mess = String.format(mess,session.getId());
        log.info(mess);
        sessionMap.put(session.getId(), session);
        session.getBasicRemote().sendText(mess);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("收到 WebSocket 关闭请求，wsId:{},", session.getId());
        sessionMap.remove(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String mess="收到 WebSocket 消息，message:%s";
        mess = String.format(mess,message);
        log.info(mess);
        session.getBasicRemote().sendText(mess);
    }

    @OnError
    public void onError(Session session) throws IOException {
        String mess="处理 WebSocket 消息出现异常，wsId:%s";
        mess = String.format(mess,session.getId());
        log.info(mess);
        session.getBasicRemote().sendText(mess);
    }

}
