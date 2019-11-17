package io.yzecho.rpcnettyetcd.protocol.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 11:45
 */
public class JsonDecoder extends LengthFieldBasedFrameDecoder {
    public JsonDecoder() {
        super(65536, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if (decode == null) {
            return null;
        }
        int dataLen = decode.readableBytes();
        byte[] bytes = new byte[dataLen];
        decode.readBytes(bytes);
        return JSON.parse(bytes);
    }
}
