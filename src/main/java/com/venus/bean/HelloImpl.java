package com.venus.bean;


import com.venus.common.RPCServcie;

/**
 * 远程调用服务的实现类
 */
@RPCServcie(Hello.class)
public class HelloImpl implements Hello{
    @Override
    public String say(String name) {
        return "Hello "+name;
    }
}
