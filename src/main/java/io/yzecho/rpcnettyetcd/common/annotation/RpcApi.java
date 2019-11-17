package io.yzecho.rpcnettyetcd.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: yzecho
 * @desc: 注解于接口的实现类上，表明该类是使用远程rpc服务的class，其中的method都会通过动态代理调用到远程的服务端
 * @date: 15/11/2019 20:58
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcApi {
}
