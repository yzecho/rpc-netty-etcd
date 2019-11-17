package io.yzecho.rpcnettyetcd.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.yzecho.rpcnettyetcd.util.ProtoStuffUtil;

import java.util.List;

/**
 * @author: yzecho
 * @desc
 * @date: 17/11/2019 10:57
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public RpcDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLen = byteBuf.readInt();
        if (dataLen < 0) {
            channelHandlerContext.close();
        }

        if (byteBuf.readableBytes() < dataLen) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        Object deserializer = ProtoStuffUtil.deserializer(data, clazz);
        list.add(deserializer);
    }
}
