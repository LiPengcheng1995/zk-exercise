package com.lpc.learn.exception;

/**
 * Package: com.lpc.learn.exception
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/20
 * Time: 19:34
 * Description:
 */
public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryException(Throwable cause) {
        super(cause);
    }
}
