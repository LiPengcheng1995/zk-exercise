package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeLatch;

import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/21
 * Time: 20:12
 * Description:
 */
public class ZKDistributeLatch implements DistributeLatch {

    @Override
    public boolean addAndWait(Long time, TimeUnit unit) {
        return false;
    }
}
