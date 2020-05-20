package com.lpc.lean.zk.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Package: com.lpc.lean.zk.common
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/5/20
 * Time: 10:54
 * Description:
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    public static <T> T getBean(String name){
        if(applicationContext != null) {
            return (T)applicationContext.getBean(name);
        }
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }
}
