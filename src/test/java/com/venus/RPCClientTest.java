package com.venus;

import com.venus.rpcclient.RPCParallelRequestThread;
import com.venus.rpcclient.RPCSendExcutor;
import com.venus.utils.CostTimeWatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rpcclient-config.xml")
public class RPCClientTest {
    @Autowired
    private RPCSendExcutor excutor;

    @Test
    public void test() throws Exception {

        int parallel = 1100;

        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(parallel);

        if(excutor == null){
            System.out.println("excutor is null");
        }
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
