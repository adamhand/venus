package com.venus.rpcclient;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RPCServerLoader {
    private static RPCServerLoader rpcServerLoader = null;
    private static final String SEPARATOR = ":";
    private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;    //Java虚拟机的可用的处理器数量
    private EventLoopGroup group = new NioEventLoopGroup(parallel);

    private ExecutorService threadPool = Executors.newFixedThreadPool(parallel);

    private RPCClientHandler rpcClientHandler = null;

    private Lock lock = new ReentrantLock();
    private Condition signal = lock.newCondition();


    private RPCServerLoader(){}

    /**
     * 单例模式得到RPCServerLoader实例
     * @return
     */
    public static RPCServerLoader getRpcServerLoaderInstance(){
        if(rpcServerLoader == null){
            synchronized (RPCServerLoader.class){
                if(rpcServerLoader == null){
                    rpcServerLoader = new RPCServerLoader();
                }
            }
        }
        return rpcServerLoader;
    }

    public void load(String serverAddress){
        String[] ipAddress = serverAddress.split(SEPARATOR);
        if(ipAddress.length == 2){
            String host = ipAddress[0];
            int port = Integer.parseInt(ipAddress[1]);

            InetSocketAddress socketAddress = new InetSocketAddress(host, port);

            threadPool.execute(new RPCSendTaskInit(group, socketAddress, this));
        }else{
            System.out.println("ipaddress length is not right");
        }
    }

    public void unload(){
        threadPool.shutdown();
        group.shutdownGracefully();
        rpcClientHandler.close();
    }

    public RPCClientHandler getRPCClientHandler(){
        try {
            lock.lock();
            if(rpcClientHandler == null){
                signal.await();
            }
            return rpcClientHandler;
        } catch (InterruptedException e) {
            System.out.println("RPCClientHandler await fails");
        } finally {
            lock.unlock();
            return rpcClientHandler;
        }
    }

    public void setRpcClientHandler(RPCClientHandler handler){
        try {
            lock.lock();
            this.rpcClientHandler = handler;

            signal.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
