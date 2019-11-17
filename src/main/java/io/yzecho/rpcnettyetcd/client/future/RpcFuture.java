package io.yzecho.rpcnettyetcd.client.future;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcResponse;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 14:18
 */
public class RpcFuture extends DefaultPromise<RpcResponse> {
    /**
     * promise要必须有一个promise
     *
     * @param executor
     */
    public RpcFuture(EventExecutor executor) {
        super(executor);
    }
}
