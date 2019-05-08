package com.venus.rpcclient;

import com.netty.NettyRPC.common.RPCRequest;
import com.netty.NettyRPC.common.RPCResponse;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.UUID;

public class RPCSendProxy implements InvocationHandler {
    private Class<?> aClass;

    public RPCSendProxy(Class<?> aClass){
        this.aClass = aClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //设置RPCRequest消息
        RPCRequest request = new RPCRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);

        //发送
        RPCClientHandler clientHandler = RPCServerLoader.getRpcServerLoaderInstance().getRPCClientHandler();
        RPCResponse response = clientHandler.sendReuest(request);

        if (response == null)
            System.out.println("response is null");

        return response.getResult();
    }
}
