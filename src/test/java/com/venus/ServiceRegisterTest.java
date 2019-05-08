package com.venus;

import com.netty.NettyRPC.zk.ServiceRegister;
import org.junit.Test;

public class ServiceRegisterTest {
    @Test
    public void serviceRegisterTest(){
        ServiceRegister register = new ServiceRegister("127.0.0.1:2181", "/zk-nettyrpc-register/data", 5000);

        register.register("serviceRegisterTest");
    }
}
