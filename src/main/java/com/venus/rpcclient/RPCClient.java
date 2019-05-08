package com.venus.rpcclient;

import com.venus.utils.CostTimeWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.CountDownLatch;

/**
 * RPC客户端主类。
 */
@ContextConfiguration(locations = "classpath:spring-rpcserver-config.xml")
public class RPCClient {
    @Autowired
    private static RPCSendExcutor excutor = new RPCSendExcutor();

//    @Autowired
//    private static RPCSendExcutor excutor;

    public static void main(String[] args) throws Exception {
        int parallel = 10;

        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(parallel);

        excutor.start();

        CostTimeWatcher watcher = new CostTimeWatcher();
        watcher.start();

        /**
         * 创建parallel个等待线程
         */
        for(int i = 0; i < parallel; i++){
            RPCParallelRequestThread client = new RPCParallelRequestThread(begin, end, excutor, i);
            new Thread(client).start();
        }

        begin.countDown();   //触发parallel个线程开始工作
        end.await();         //工作完成

        watcher.stop();

        float costTime = watcher.getCostTime();

        System.out.println("cost time : "+costTime+" s");

        excutor.stop();
    }
}
