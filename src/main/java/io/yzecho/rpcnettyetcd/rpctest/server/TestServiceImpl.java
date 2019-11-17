package io.yzecho.rpcnettyetcd.rpctest.server;

import io.yzecho.rpcnettyetcd.common.annotation.RpcService;
import io.yzecho.rpcnettyetcd.rpctest.common.TestService;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 16:11
 */
@RpcService
public class TestServiceImpl implements TestService {
    @Override
    public String sayHello() {
        return "hello rpc";
    }
}
