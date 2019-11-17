package io.yzecho.rpcnettyetcd.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.yzecho.rpcnettyetcd.util.ProtoStuffUtil;

/**
 * @author: yzecho
 * @desc
 * @date: 17/11/2019 10:58
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    public RpcEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)) {
            byte[] data = ProtoStuffUtil.serializer(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
