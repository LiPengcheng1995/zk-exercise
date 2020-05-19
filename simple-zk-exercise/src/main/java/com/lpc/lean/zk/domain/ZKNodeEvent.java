package com.lpc.lean.zk.domain;

import lombok.Data;

/**
 * Package: com.lpc.lean.zk.domain
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/19
 * Time: 17:35
 * Description:
 */
@Data
public class ZKNodeEvent {
    private String path;
    private String newValue;
}
