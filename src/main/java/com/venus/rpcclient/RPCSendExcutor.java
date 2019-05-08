package com.venus.rpcclient;

import com.netty.NettyRPC.zk.ServiceDiscovery;
import org.springframework.cglib.proxy.Proxy;

public class RPCSendExcutor {
    private RPCServerLoader loader = RPCServerLoader.getRpcServerLoaderInstance();
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RPCSendExcutor(){}

    public RPCSendExcutor(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public void start(){
        if (serviceDiscovery != null){
            serverAddress = serviceDiscovery.discover();
        }else {
            System.out.println("serviceDiscovery fails");
        }

        loader.load(serverAddress);
    }

    public void stop(){
        loader.unload();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RPCSendProxy(interfaceClass)
        );
    }
}
