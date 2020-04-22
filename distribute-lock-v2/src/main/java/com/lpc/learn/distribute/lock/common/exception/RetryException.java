package com.lpc.learn.distribute.lock.common.exception;

/**
 * Package: com.lpc.learn.distribute.lock.common.exception
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 19:22
 * Description:
 */
public class RetryException extends DistributeException {
    public RetryException() {
    }

    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryException(Throwable cause) {
        super(cause);
    }

    public RetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
