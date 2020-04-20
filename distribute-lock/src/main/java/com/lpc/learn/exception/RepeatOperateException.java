package com.lpc.learn.exception;

/**
 * Package: com.lpc.learn.exception
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/20
 * Time: 19:24
 * Description:
 */
public class RepeatOperateException extends RuntimeException{
    public RepeatOperateException(String message) {
        super(message);
    }
}
