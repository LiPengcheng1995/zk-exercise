package com.lpc.lean.zk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Package: com.lpc.lean.zk.controller
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/20
 * Time: 09:30
 * Description:
 */
@Slf4j
@RestController
@RequestMapping("/zk")
public class ZKController {
    @GetMapping("/add")
    public String add(HttpServletResponse response, String path){
        Cookie cookie = new Cookie("path",path);
        response.addCookie(cookie);
        return path;
    }
    @GetMapping("/remove")
    public String remove(HttpServletResponse response){
        Cookie cookie = new Cookie("path","");
        response.addCookie(cookie);
        return "完成修改";
    }
}
