package com.venus.rpcclient;

import com.venus.common.RPCDecoder;
import com.venus.common.RPCEncoder;
import com.venus.common.RPCRequest;
import com.venus.common.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class RPCSendTaskInit implements Runnable {
    private EventLoopGroup eventExecutors;
    private InetSocketAddress serverAddress;
    private RPCServerLoader loader;

    public RPCSendTaskInit(EventLoopGroup eventExecutors, InetSocketAddress serverAddress, RPCServerLoader loader){
        this.eventExecutors = eventExecutors;
        this.serverAddress = serverAddress;
        this.loader = loader;
    }

    @Override
    public void run() {
        Bootstrap b = new Bootstrap();
        b.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new RPCEncoder(RPCRequest.class))       //对request进行序列化
                                .addLast(new RPCDecoder(RPCResponse.class))      //对response进行反序列化
                                .addLast(new RPCClientHandler());                 //处理
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true) ;
        ChannelFuture f = b.connect(serverAddress);
        f.addListener(new ChannelFutureListener() {
            //监听消息写入网络Socket是否成功
            @Override
            public void operationComplete(final ChannelFuture channelFuture){
                if (channelFuture.isSuccess()) {
                    RPCClientHandler handler = channelFuture.channel().pipeline().get(RPCClientHandler.class);
                    RPCSendTaskInit.this.loader.setRpcClientHandler(handler);
                }
            }
        });
    }
}
