package io.yzecho.rpcnettyetcd.protocol.packet;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 11:09
 */
@Data
@NoArgsConstructor
public class RpcResponse {
    private long requestId;
    private Throwable exception;
    private Object result;

    public RpcResponse(long requestId) {
        this.requestId = requestId;
    }
}
