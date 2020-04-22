package com.lpc.learn.distribute.lock.common.exception;

/**
 * Package: com.lpc.learn.distribute.lock.common.exception
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 19:23
 * Description:
 */
public class RepeatOperateException extends DistributeException {

    public RepeatOperateException() {
    }

    public RepeatOperateException(String message) {
        super(message);
    }

    public RepeatOperateException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatOperateException(Throwable cause) {
        super(cause);
    }

    public RepeatOperateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
