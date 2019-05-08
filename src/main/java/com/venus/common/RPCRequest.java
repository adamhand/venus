package com.venus.common;

import lombok.Data;

/**
 * RPC请求消息。
 */
@Data
public class RPCRequest {
    private String requestId;       //RPC请求消息序列号
    private String className;       //RPC请求服务类名
    private String methodName;      //RPC请求服务方法名
    private Class<?>[] paramTypes;  //RPC请求服务参数类型，用于代理时反射调用。
    private Object[] params;        //RPC请求服务参数列表，用于代理时反射调用。
}
