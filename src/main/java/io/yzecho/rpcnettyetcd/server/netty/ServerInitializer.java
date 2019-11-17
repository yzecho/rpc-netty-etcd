package io.yzecho.rpcnettyetcd.server.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.yzecho.rpcnettyetcd.protocol.codec.JsonDecoder;
import io.yzecho.rpcnettyetcd.protocol.codec.JsonEncoder;
import io.yzecho.rpcnettyetcd.protocol.codec.RpcDecoder;
import io.yzecho.rpcnettyetcd.protocol.codec.RpcEncoder;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcRequest;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcResponse;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 10:57
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new RpcDecoder(RpcRequest.class));
        channel.pipeline().addLast(new RpcEncoder(RpcResponse.class));
//        channel.pipeline().addLast(new JsonDecoder());
//        channel.pipeline().addLast(new JsonEncoder());
        channel.pipeline().addLast(new ServerHandler());
    }
}
