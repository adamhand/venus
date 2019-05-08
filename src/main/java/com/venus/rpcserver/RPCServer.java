package com.venus.rpcserver;

import com.netty.NettyRPC.common.*;
import com.netty.NettyRPC.zk.ServiceRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC服务端主类，进行spring的装配bean并实现服务端的业务处理逻辑。
 */
public class RPCServer implements ApplicationContextAware, InitializingBean {
    private String serverAddress;
    private ServiceRegister serviceRegister;
    private Map<String, Object> map = new HashMap<>();
    private int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private static final String SEPARATOR = ":";

    public RPCServer(String serverAddress, ServiceRegister serviceRegister) {
        this.serverAddress = serverAddress;
        this.serviceRegister = serviceRegister;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RPCServcie.class); // 获取所有带有 RpcService 注解的 Spring Bean
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RPCServcie.class).value().getName();
                map.put(interfaceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(parallel);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new RPCDecoder(RPCRequest.class))
                                    .addLast(new RPCEncoder(RPCResponse.class))
                                    .addLast(new RPCServerHandler(map));
                        }
                    });

            String[] addressArray = serverAddress.split(SEPARATOR);
            if(addressArray.length == 2) {
                String host = addressArray[0];
                int port = Integer.parseInt(addressArray[1]);

                ChannelFuture f = b.bind(host, port).sync();

                if(serviceRegister != null){
                    serviceRegister.register(serverAddress);
                }else {
                    System.out.println("serviceRegister fails");
                }

                /**
                 * 为什么这句话写在register前面就注册不上呢？
                 */
                f.channel().closeFuture().sync();
            }else{
                System.out.println("ip address length is not correct");
            }

        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
