package com.venus;

import com.netty.NettyRPC.zk.ServiceDiscovery;
import org.junit.Test;

public class ServiceDiscovertyTest {
    @Test
    public void test(){
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181", "/zk-nettyrpc-register", 5000);

        String serviceAddress = serviceDiscovery.discover();

        System.out.println(serviceAddress);
    }
}
