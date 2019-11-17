package io.yzecho.rpcnettyetcd.server;

import io.yzecho.rpcnettyetcd.common.annotation.RpcApi;
import io.yzecho.rpcnettyetcd.common.annotation.RpcService;
import io.yzecho.rpcnettyetcd.common.etcd.ServiceRegistry;
import io.yzecho.rpcnettyetcd.common.etcd.impl.ServiceRegistryImpl;
import io.yzecho.rpcnettyetcd.server.netty.NettyServer;
import io.yzecho.rpcnettyetcd.server.netty.ServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Set;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 10:40
 */
@Slf4j
public class ServerMain {
    private static final int DEFAULT_SERVER_PORT = 8890;

    private ServiceRegistry serviceRegistry;

    private int port;

    private final String packagePath;


    public ServerMain(String packagePath) {
        this(packagePath, new ServiceRegistryImpl());
    }

    public ServerMain(String packagePath, ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.packagePath = packagePath;
        this.port = System.getProperty("server.port") == null ? DEFAULT_SERVER_PORT : Integer.parseInt(System.getProperty("server.port"));
    }

    public void start() {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcService.class);
        System.out.println(classes);
        classes.forEach(clazz -> {
            try {
                Class<?>[] interfaces = clazz.getInterfaces();
                String clazzName = clazz.getName();

                // 简单实现，所以只是获取了第一个interface的name，实际上并不准确可能有误
                if (interfaces != null && interfaces.length > 0) {
                    clazzName = interfaces[0].getName();
                }
                // 注册的是接口名和服务实例
                // clazzMap是用来保存一个实例对象，相当于服务端实例
                ServerHandler.clazzMap.put(clazzName, clazz.getDeclaredConstructor().newInstance());
                serviceRegistry.register(clazzName, port);
                log.info("register success");
            } catch (Exception e) {
                log.info("register service failed:" + e.getLocalizedMessage(), e);
            }
        });
        // 新起线程的话程序会退出
        new NettyServer(port).start();
    }
}
