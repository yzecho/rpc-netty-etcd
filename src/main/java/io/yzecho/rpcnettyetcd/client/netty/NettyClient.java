package io.yzecho.rpcnettyetcd.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: yzecho
 * @desc: 客户端，代理通过该端发送请求，考虑保持长连接，准备一个client与一个地址绑定
 * 心跳设置，当服务不可用时删除clientMap中对应的endpoint
 * @date: 16/11/2019 10:36
 */
@Slf4j
public class NettyClient {
    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private ChannelFuture channelFuture;

    private String host;

    private int port;

    public NettyClient(String host, int port) {
        this(host, port, Epoll.isAvailable() ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1));

    }

    public NettyClient(String host, int port, EventLoopGroup eventLoopGroup) {
        this.host = host;
        this.port = port;
        this.eventLoopGroup = eventLoopGroup;
    }

    public ChannelFuture connectChannel() {
        if (channelFuture == null) {
            channelFuture = new Bootstrap().group(eventLoopGroup)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                    .handler(new ClientInitializer())
                    .connect(host, port)
                    .addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            channel = future.channel();
                            log.info("start a client to " + host + ":" + port);
                            channel.closeFuture().addListener((ChannelFutureListener) closefuture -> {
                                log.info("stop the client to " + host + ":" + port);
                            });
                        } else {
                            log.error("start a Client faild", future.cause());
                        }
                    });
        }
        return channelFuture;
    }

    public Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        channelFuture = connectChannel();
        return channelFuture.channel();
    }
}
