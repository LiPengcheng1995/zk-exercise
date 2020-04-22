package com.lpc.learn.distribute.lock.zk;

import com.lpc.learn.distribute.lock.common.DistributeQueueNode;

/**
 * Package: com.lpc.learn.distribute.lock.zk
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 18:08
 * Description:
 */
public class ZKDistributeQueueNode implements DistributeQueueNode {

    private String baseId;

    private String localId;

    private String surffixId;

    public ZKDistributeQueueNode() {
    }

    public ZKDistributeQueueNode(String baseId, String localId, String surffixId) {
        this.baseId = baseId;
        this.localId = localId;
        this.surffixId = surffixId;
    }

    public ZKDistributeQueueNode(String input) {
        int index = input.lastIndexOf(PATH_SEPRATOR);
        baseId = input.substring(0, index);

        String surffix = input.substring(index);
        String[] array = surffix.split(INNER_SEPRATOR);
        localId = array[0];
        surffixId = array[1];
    }

    @Override
    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    @Override
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    @Override
    public String getSurffixId() {
        return surffixId;
    }

    public void setSurffixId(String surffixId) {
        this.surffixId = surffixId;
    }

    @Override
    public String getId() {
        return baseId + PATH_SEPRATOR + localId + INNER_SEPRATOR + surffixId;
    }
}
