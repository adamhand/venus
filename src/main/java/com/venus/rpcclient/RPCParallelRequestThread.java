package com.venus.rpcclient;


import com.venus.bean.Hello;

import java.util.concurrent.CountDownLatch;

public class RPCParallelRequestThread implements Runnable {
    private CountDownLatch begin;
    private CountDownLatch end;
    private RPCSendExcutor excutor;
    private int taskNum;

    public RPCParallelRequestThread(CountDownLatch begin, CountDownLatch end, RPCSendExcutor excutor, int taskNum){
        this.begin = begin;
        this.end = end;
        this.excutor = excutor;
        this.taskNum = taskNum;
    }


    @Override
    public void run() {
        try {
            begin.await();

            Hello hello = excutor.getProxy(Hello.class);
            String result = hello.say("world "+taskNum);
            System.out.println(result);

            end.countDown();
        }catch (InterruptedException e) {
            System.out.println("RPCParallelRequestThread has some errors");
        }
    }
}
