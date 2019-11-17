package io.yzecho.rpcnettyetcd.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: yzecho
 * @desc: 注解于实现了接口的服务类上，表示该类是用于提供rpc服务的class，其中的method都会被注册到etcd中
 * @date: 15/11/2019 21:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {
}
