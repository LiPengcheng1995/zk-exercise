package com.lpc.learn.distribute.lock.common.exception;

/**
 * Package: com.lpc.learn.distribute.lock.common.exception
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 18:15
 * Description:
 */
public class DistributeException extends RuntimeException {
    public DistributeException() {
    }

    public DistributeException(String message) {
        super(message);
    }

    public DistributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributeException(Throwable cause) {
        super(cause);
    }

    public DistributeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
