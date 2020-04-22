package com.lpc.learn.distribute.lock.common;

/**
 * Package: com.lpc.learn.distribute.lock.common
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 17:56
 * Description:
 */
public interface DistributeQueueNode {

    String PATH_SEPRATOR = "/";

    String INNER_SEPRATOR = "-";

    String getBaseId();

    String getLocalId();

    String getSurffixId();

    String getId();

}
