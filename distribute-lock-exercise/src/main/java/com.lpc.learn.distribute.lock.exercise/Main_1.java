package com.lpc.learn.distribute.lock.exercise;

import com.lpc.learn.distribute.lock.zk.ZKLock;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Package: com.lpc.learn.distribute.lock.exercise
 * User: 李鹏程
 * Email: lipengcheng3@jd.com
 * Date: 2020/4/22
 * Time: 20:55
 * Description:
 */
public class Main_1 {
    public static ZKLock zkLock;
    public static volatile boolean ifStop = false;
    public static int TASK_NUM=1;

    static {
        try {
            zkLock = new ZKLock("127.0.0.1:2181",2000,"/localTest");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService service = Executors.newFixedThreadPool(TASK_NUM);
        for (int i=0;i<TASK_NUM;i++){
            service.submit(new TestRun("task_"+i));
        }
        System.in.read();
        ifStop=true;

    }


    public static class TestRun implements Runnable{

        String threadId;

        public TestRun(String threadId) {
            this.threadId = threadId;
        }

        @Override
        public void run() {
            while (true){
                if (ifStop){
                    System.out.println("子任务退出:"+threadId);
                    return;
                }
                boolean ifLock = false;
                try {
                    ifLock = zkLock.lock(1000L, TimeUnit.MICROSECONDS);
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }

                if (ifLock){
                    long x = Math.round(Math.random()*1000);
                    System.out.println("======开始进行子任务:"+threadId+",睡眠时间:"+x);
                    try{
                        Thread.sleep(x);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.println("======执行完成子任务:"+threadId);
                    zkLock.release();
                }else {
                    System.out.println(">>>>子任务等待超时，"+threadId);
                }
            }
        }
    }
}
