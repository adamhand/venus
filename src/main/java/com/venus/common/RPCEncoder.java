package com.venus.common;

import com.venus.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC的编码操作。实现netty的MessageToByteEncoder，使用protostuff
 * 实现序列化。
 */
public class RPCEncoder extends MessageToByteEncoder {
    private Class<?> aClass;

    public RPCEncoder(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        if(aClass.isInstance(o)){
            byte[] data = SerializeUtil.serializer(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
