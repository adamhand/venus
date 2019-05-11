package com.venus.rpcserver;

import com.venus.common.RPCRequest;
import com.venus.common.RPCResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.Map;

public class RPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    //存放服务名与服务对象之间的关系
    private final Map<String, Object> map;

    public RPCServerHandler(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCRequest request){
        RPCResponse response = new RPCResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = RPCChannelHander(request);
            response.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            response.setError(e);
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 服务端处理函数，。使用Cglib实现Java反射。
     * @param request
     * @return
     * @throws Exception
     */
    private Object RPCChannelHander(RPCRequest request) throws Exception {
        String serviceName = request.getClassName();
        Object serviceBean = map.get(serviceName);
        if(serviceBean == null){
            throw new RuntimeException("can not found service bean");
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object[] params = request.getParams();

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, paramTypes);
        return serviceFastMethod.invoke(serviceBean, params);
    }
}
