package io.yzecho.rpcnettyetcd.client.future;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;

/**
 * @author: yzecho
 * @desc 保存client的请求future
 * @date: 16/11/2019 14:16
 */
public class FutureHolder {
    private static FastThreadLocal<HashMap<Long, RpcFuture>> futureHolder = new FastThreadLocal<HashMap<Long, RpcFuture>>() {
        @Override
        protected HashMap<Long, RpcFuture> initialValue() throws Exception {
            return new HashMap<>(16);
        }
    };

    public static void registerFuture(long requestId, RpcFuture future) {
        futureHolder.get().put(requestId, future);
    }

    public static RpcFuture getAndRemoveFuture(long requestId) {
        return futureHolder.get().remove(requestId);
    }
}
