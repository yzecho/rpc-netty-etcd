package io.yzecho.rpcnettyetcd.client.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.yzecho.rpcnettyetcd.client.ClientMain;
import io.yzecho.rpcnettyetcd.client.future.FutureHolder;
import io.yzecho.rpcnettyetcd.client.future.RpcFuture;
import io.yzecho.rpcnettyetcd.client.netty.NettyClient;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcRequest;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 14:30
 */
@Slf4j
public class ProxyIntercepter implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] parameters, MethodProxy methodProxy) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        Class clazz = method.getDeclaringClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        //存在接口时使用的是接口名称
        String clazzName = clazz.getName();
        if (interfaces != null && interfaces.length > 0) {
            clazzName = interfaces[0].getName();
        }
        rpcRequest.setClassName(clazzName);
        rpcRequest.setServiceName(method.getName());
        rpcRequest.setParamTypes(method.getParameterTypes());
        rpcRequest.setParams(parameters);

        NettyClient client = ClientMain.getClient(rpcRequest.getClassName());

        RpcFuture rpcFuture;
        if (client != null) {
            ChannelFuture channelFuture = client.connectChannel();
            rpcFuture = new RpcFuture(channelFuture.channel().eventLoop());
            if (channelFuture.isSuccess()) {
                sendRequest(rpcRequest, rpcFuture, channelFuture);
            } else {
                channelFuture.addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        sendRequest(rpcRequest, rpcFuture, future);
                    } else {
                        log.error("send request error ", future.cause());
                    }
                });
            }
            //这里没有用listener & getNow的方式获取主要是考虑客户端本身非异步的情形，同时是为了简便实现。
            RpcResponse rpcResponse = rpcFuture.get(3, TimeUnit.SECONDS);
            if (rpcResponse.getException() == null) {
                return rpcResponse.getResult();
            } else {
                throw rpcResponse.getException();
            }
        } else {
            log.error("no rpcService is available :" + rpcRequest.getClassName());
            return null;
        }
    }

    private void sendRequest(RpcRequest rpcRequest, RpcFuture rpcFuture, ChannelFuture channelFuture) {
        channelFuture.channel().writeAndFlush(rpcRequest)
                .addListener((ChannelFutureListener) writefuture -> {
                    if (writefuture.isSuccess()) {
                        FutureHolder.registerFuture(rpcRequest.getRequestId(), rpcFuture);
                        log.info("send request success");
                    } else {
                        rpcFuture.tryFailure(writefuture.cause());
                        log.error("send request failed", writefuture.cause());
                    }
                });
    }
}
