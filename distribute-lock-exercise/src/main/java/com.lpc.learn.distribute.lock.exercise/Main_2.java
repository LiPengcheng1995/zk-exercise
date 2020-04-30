package com.lpc.learn.distribute.lock.exercise;

import com.lpc.learn.distribute.lock.common.DistributeQueueNode;
import com.lpc.learn.distribute.lock.zk.ZKQueueClient;

import java.io.IOException;

/**
 * Package: com.lpc.learn.distribute.lock.exercise
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/30
 * Time: 10:15
 * Description:
 */
public class Main_2 {
    public static void main(String[] args) throws IOException {
        ZKQueueClient client = new ZKQueueClient("127.0.0.1:2181", 20000, "/localTest");
        DistributeQueueNode node = null;
        while (true){
            try {
                node = client.add();
            }catch (Exception e){
                client.refresh();
                continue;
            }
            break;
        }

        System.out.println(node.getId());
        DistributeQueueNode node1 = null;
        while (true){
            try {
                node1 = client.add();
            }catch (Exception e){
                client.refresh();
                continue;
            }
            break;
        }
        System.out.println(node1.getId());


    }
}
