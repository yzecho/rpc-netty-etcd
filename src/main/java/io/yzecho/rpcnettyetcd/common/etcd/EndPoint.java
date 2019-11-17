package io.yzecho.rpcnettyetcd.common.etcd;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: yzecho
 * @desc
 * @date: 15/11/2019 21:54
 */
@Data
@AllArgsConstructor
public class EndPoint {
    private String host;
    private int port;

    @Override
    public boolean equals(Object o) {
        if (o instanceof EndPoint) {
            return ((EndPoint) o).host.equals(host) && ((EndPoint) o).port == port;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public int hashCode() {
        return host.hashCode() + port;
    }
}
