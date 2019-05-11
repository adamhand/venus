package com.venus;

import com.venus.rpcclient.RPCSendExcutor;
import com.venus.zk.ServiceDiscovery;
import org.junit.Test;

public class RPCSentExcutorTest {
    @Test
    public void test(){
        RPCSendExcutor excutor = new RPCSendExcutor();
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181", "/zk-nettyrpc-register", 5000);
        excutor.setServiceDiscovery(serviceDiscovery);

        excutor.start();
    }
}
