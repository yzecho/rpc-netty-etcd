package io.yzecho.rpcnettyetcd.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 10:38
 */
@Slf4j
public class NettyServer {
    private static final int DEFAULT_BOSS_EVENT_LOOP_GROUP_SIZE = 1;
    private static final int DEFAULT_WORKER_EVENT_LOOP_GROUP_SIZE = 4;
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(DEFAULT_BOSS_EVENT_LOOP_GROUP_SIZE) : new NioEventLoopGroup(DEFAULT_BOSS_EVENT_LOOP_GROUP_SIZE);
        EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(DEFAULT_WORKER_EVENT_LOOP_GROUP_SIZE) : new NioEventLoopGroup(DEFAULT_WORKER_EVENT_LOOP_GROUP_SIZE);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new ServerInitializer())
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        log.info("server start at port:" + port);
        try {
            // 绑定端口，接收连接，同步等待服务器Socket关闭
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("server over");
        }
    }

}
