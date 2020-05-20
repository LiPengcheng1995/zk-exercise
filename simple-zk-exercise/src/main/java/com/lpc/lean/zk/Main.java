package com.lpc.lean.zk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Package: com.lpc.lean.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/30
 * Time: 18:06
 * Description: 进行zk的最基本功能的实践，不要贪多了。
 * 使用 SpringBoot 启动吧，带上完整的日志功能，方便使用
 */
@EnableWebSocket
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }
}
