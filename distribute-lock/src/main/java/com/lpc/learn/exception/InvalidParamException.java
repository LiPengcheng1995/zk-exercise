package com.lpc.learn.exception;

/**
 * Package: com.lpc.learn.exception
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 18:01
 * Description:
 */
public class InvalidParamException extends RuntimeException {
    public InvalidParamException(String message) {
        super(message);
    }
}
