package com.venus.rpcclient;

import com.venus.common.RPCRequest;
import com.venus.common.RPCResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

/**
 * RPC客户端处理类。
 */
public class RPCClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    private RPCResponse response;
    private Channel channel;
    private SocketAddress serverAddress;

    private final Object obj = new Object();

    public RPCClientHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.serverAddress = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse) {
        this.response = rpcResponse;

        synchronized (obj){
            obj.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 发送RPC请求。被代理函数RPCProxy调用。
     * @param request
     * @return
     * @throws Exception
     */
    public RPCResponse sendReuest(RPCRequest request) throws InterruptedException {
        channel.writeAndFlush(request).sync();

        synchronized (obj){
            obj.wait();
        }

        return response;
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
