package com.venus.common;

import com.venus.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 实现RPC的解码操作。继承netty的ByteToMessageDecoder，使用
 * protostuff实现反序列化。
 */
public class RPCDecoder extends ByteToMessageDecoder {
    private Class<?> aClass;

    public RPCDecoder(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        list.add(SerializeUtil.deserializer(bytes, aClass));
    }
}
