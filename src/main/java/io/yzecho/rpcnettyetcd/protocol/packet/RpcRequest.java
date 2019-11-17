package io.yzecho.rpcnettyetcd.protocol.packet;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 11:05
 */
@Data
public class RpcRequest {
    private static AtomicLong atomicLong = new AtomicLong(0);
    private long requestId;
    private String className;
    private String serviceName;
    private Class<?>[] paramTypes;
    private Object[] params;

    public RpcRequest() {
        this.requestId = atomicLong.getAndIncrement();
    }

    public RpcRequest(long requestId) {
        this.requestId = requestId;
    }
}
