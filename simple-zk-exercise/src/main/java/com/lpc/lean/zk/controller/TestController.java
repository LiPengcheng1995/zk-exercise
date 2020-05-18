package com.lpc.lean.zk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Package: com.lpc.lean.zk.controller
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/18
 * Time: 17:34
 * Description:
 */
@Slf4j
@RestController
@RequestMapping(path = "/test")
public class TestController {

    @GetMapping(path = "")
    public String test() {
        log.info("收到一次请求");
        return "haha";
    }
}
