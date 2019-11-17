package io.yzecho.rpcnettyetcd.protocol.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 11:47
 */
public class JsonEncoder extends MessageToMessageEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, List out) throws Exception {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(msg);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }
}
