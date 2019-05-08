package com.venus.common;

import lombok.Data;

/**
 * RPC应答消息。
 */
@Data
public class RPCResponse {
    private String requestId;    //接收到的RPC请求消息的序列号
    private Throwable error;     //是否出现错误或异常
    private Object result;       //RPC执行结果

    /**
     * RPC请求过程是否发生错误
     * @return
     */
    public boolean hasError() {
        return error != null;
    }
}
