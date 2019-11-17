package io.yzecho.rpcnettyetcd.server.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcResponse;
import io.yzecho.rpcnettyetcd.protocol.packet.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 11:05
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    public static Map<String, Object> clazzMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcrRequest) throws Exception {
        log.info("recieve a request id:" + rpcrRequest.getRequestId());

        RpcResponse response = handler(rpcrRequest);
        channelHandlerContext.writeAndFlush(response).addListener(future -> {
            if (!future.isSuccess()) {
                log.error(future.cause().getLocalizedMessage());
            }
        });
    }

    private RpcResponse handler(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse(rpcRequest.getRequestId());
        try {
            Class<?> clazz = Class.forName(rpcRequest.getClassName());

            Object val = clazzMap.get(rpcRequest.getClassName());
            if (val == null) {
                clazzMap.put(rpcRequest.getClassName(), clazz.getDeclaredConstructor().newInstance());
                val = clazzMap.get(rpcRequest.getClassName());
            }

            String methodName = rpcRequest.getServiceName();
            Class<?>[] paramTypes = rpcRequest.getParamTypes();
            Object[] params = rpcRequest.getParams();
            FastClass fastClass = FastClass.create(clazz);
            FastMethod method = fastClass.getMethod(methodName, paramTypes);
            // 与Spring联合使用时应该调用ApplicationContext里面存在的bean
            Object result = method.invoke(val, params);

            rpcResponse.setResult(result);

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            if ("远程主机强迫关闭了一个现有的连接。".equals(cause.getLocalizedMessage())) {
                log.info("一个客户端连接断开");
            }
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
