package io.yzecho.rpcnettyetcd.common.etcd;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: yzecho
 * @desc
 * @date: 15/11/2019 21:52
 */
public interface ServiceRegistry {

    /**
     * 服务注册
     *
     * @param serviceName
     * @param port
     */
    void register(String serviceName, int port) throws ExecutionException, InterruptedException, Exception;

    /**
     * 服务查询
     *
     * @param serviceName
     * @return
     */
    List<EndPoint> search(String serviceName) throws Exception;
}
