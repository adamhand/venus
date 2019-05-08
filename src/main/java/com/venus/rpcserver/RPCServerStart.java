package com.venus.rpcserver;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 开启服务端。
 */
public class RPCServerStart {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-rpcserver-config.xml");
    }
}
