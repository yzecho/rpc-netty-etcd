package io.yzecho.rpcnettyetcd.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.yzecho.rpcnettyetcd.client.netty.NettyClient;
import io.yzecho.rpcnettyetcd.common.annotation.RpcApi;
import io.yzecho.rpcnettyetcd.common.etcd.EndPoint;
import io.yzecho.rpcnettyetcd.common.etcd.ServiceRegistry;
import io.yzecho.rpcnettyetcd.common.etcd.impl.ServiceRegistryImpl;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 14:44
 */
@Slf4j
public class ClientMain {
    private ServiceRegistry serviceRegistry;

    /**
     * 设置 一个endpoint使用client，netty高效理论上满足使用
     */
    private static Map<EndPoint, NettyClient> clientMap = new ConcurrentHashMap<>();
    private static Map<String, List<EndPoint>> serviceMap = new ConcurrentHashMap<>();

    private String packagePath;

    private static Random random = new Random();

    public ClientMain(String packagePath) {
        this.packagePath = packagePath;
        this.serviceRegistry = new ServiceRegistryImpl();
    }

    public void start() {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcApi.class);
        EventLoopGroup eventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);
        // 定时任务线程池，定时更新服务列表，设置为3分钟
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2);
        classes.forEach(clazz -> executorService.scheduleAtFixedRate(() -> {
            try {
                // 拿到当前仍在注册中心中的相应服务列表
                // 删除掉对应失效的endpoint
                Class<?>[] interfaces = clazz.getInterfaces();
                String className = clazz.getName();
                if (interfaces != null && interfaces.length > 0) {
                    className = interfaces[0].getName();
                }
                log.info("类名为:{}", classes);
                List<EndPoint> list = serviceRegistry.search(className);
                log.info("list:{}", list);
                serviceMap.put(className, list);
                list.forEach(endPoint -> {
                    if (clientMap.get(endPoint) == null) {
                        //所有的Client共用一个EventLoopGroup
                        NettyClient client = new NettyClient(endPoint.getHost(), endPoint.getPort(), eventLoopGroup);
                        clientMap.put(endPoint, client);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3 * 60, TimeUnit.SECONDS));

    }

    public static NettyClient getClient(String serviceName) {
        List<EndPoint> endPoints = serviceMap.get(serviceName);
        // 简单的负载均衡，只使用了随机选择
        if (endPoints != null) {
            EndPoint endPoint = endPoints.get(random.nextInt(endPoints.size()));
            return clientMap.get(endPoint);
        }
        return null;
    }
}
