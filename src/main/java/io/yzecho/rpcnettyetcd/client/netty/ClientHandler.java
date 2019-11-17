package io.yzecho.rpcnettyetcd.client.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.yzecho.rpcnettyetcd.client.future.FutureHolder;
import io.yzecho.rpcnettyetcd.client.future.RpcFuture;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 12:20
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        log.info("receive a response,response id:" + rpcResponse.getRequestId());
        RpcFuture future = FutureHolder.getAndRemoveFuture(rpcResponse.getRequestId());
        if (future != null) {
            future.setSuccess(rpcResponse);
        }
    }
}
