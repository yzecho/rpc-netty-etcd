package io.yzecho.rpcnettyetcd.client.proxy;

import org.springframework.cglib.proxy.Enhancer;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 14:28
 */
public class ProxyFactory {
    public static <T> T create(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ProxyIntercepter());
        return (T) enhancer.create();
    }
}
