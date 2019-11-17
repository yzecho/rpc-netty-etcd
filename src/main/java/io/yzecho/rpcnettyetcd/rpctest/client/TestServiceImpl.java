package io.yzecho.rpcnettyetcd.rpctest.client;

import io.yzecho.rpcnettyetcd.common.annotation.RpcApi;
import io.yzecho.rpcnettyetcd.rpctest.common.TestService;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 16:12
 */
@RpcApi
public class TestServiceImpl implements TestService {
    @Override
    public String sayHello() {
        return null;
    }
}
