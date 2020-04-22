package com.lpc.learn.distribute.lock.zk;

import java.util.Comparator;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 18:27
 * Description:
 */
public class ZKDistributeQueueNodeComparator implements Comparator<ZKDistributeQueueNode> {
    @Override
    public int compare(ZKDistributeQueueNode o1, ZKDistributeQueueNode o2) {
        Integer o1Serial = Integer.valueOf(o1.getSurffixId());
        Integer o2Serial = Integer.valueOf(o2.getSurffixId());
        return o1Serial-o2Serial;
    }
}
