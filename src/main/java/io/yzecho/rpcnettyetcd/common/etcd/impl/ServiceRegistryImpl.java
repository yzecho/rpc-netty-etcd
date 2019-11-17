package io.yzecho.rpcnettyetcd.common.etcd.impl;

import com.google.protobuf.ByteString;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import io.yzecho.rpcnettyetcd.common.ThreadPool;
import io.yzecho.rpcnettyetcd.common.etcd.EndPoint;
import io.yzecho.rpcnettyetcd.common.etcd.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * @author: yzecho
 * @desc
 * @date: 15/11/2019 21:23
 */
@Slf4j
public class ServiceRegistryImpl implements ServiceRegistry {

    private static final String ROOTPATH = "etcdrpc";
    /**
     * 服务注册中心
     */
    private static final String DEFAULT_ADDRESS = "http://localhost:2379";

    private KV kv;
    /**
     * 租约(用于设置过时时间)
     */
    private Lease lease;
    private static final long LEASETTL = 60;

    /**
     * 租约对应的id
     */
    private long leaseId;

    public ServiceRegistryImpl() {
        this(DEFAULT_ADDRESS);
    }

    public ServiceRegistryImpl(String address) {
        address = address != null ? address : DEFAULT_ADDRESS;

        if (System.getProperty("etcd.url") != null) {
            address = System.getProperty("etcd.url");
        }

        // 创建client
        Client client = Client.builder().endpoints(address).build();

        this.kv = client.getKVClient();
        this.lease = client.getLeaseClient();
        try {
            // 授予租约，LEASETTL秒后自动删除，返回租约的id。
            this.leaseId = lease.grant(LEASETTL).get().getID();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        keepAlive();
    }

    /**
     * 发送心跳到etcd，表明该host的存活状态
     */
    private void keepAlive() {
        ThreadPool.INSTANCE.submit(() -> {
            lease.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
                    log.info("LeaderSelector lease keeps alive for {}s", leaseKeepAliveResponse.getTTL());
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error(throwable.getLocalizedMessage());
                }

                @Override
                public void onCompleted() {
                    log.info("LeaderSelector lease renewal completed! start canceling task.");
                }
            });
        });
    }

    /**
     * 注册类名，一个类对应一个client
     *
     * @param serviceName
     * @param port
     */
    @Override
    public void register(String serviceName, int port) throws Exception {
        String strKey = MessageFormat.format("/{0}/{1}/{2}:{3}", ROOTPATH, serviceName, getHostIp(), String.valueOf(port));
        ByteSequence key = ByteSequence.from(ByteString.copyFromUtf8(strKey));
        // 目前只需要创建这个key，对应的vaule暂不使用，先留空
        ByteSequence value = ByteSequence.from(ByteString.copyFromUtf8(""));
        // 等待put结束之后继续执行
        kv.put(key, value, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        log.info("Register a new service at:" + strKey);
    }

    private String getHostIp() throws UnknownHostException {
        return Inet4Address.getLocalHost().getHostAddress();
    }

    @Override
    public List<EndPoint> search(String serviceName) throws Exception {
        String strKey = MessageFormat.format("/{0}/{1}", ROOTPATH, serviceName);
        log.info("start to find service,Name:" + strKey);

        ByteSequence key = ByteSequence.from(ByteString.copyFromUtf8(strKey));
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();
        List<EndPoint> list = new ArrayList<>();
        response.getKvs().forEach(keyValue -> {
            String s = keyValue.getKey().toString(StandardCharsets.UTF_8);
            int index = s.lastIndexOf("/");
            String endPointStr = s.substring(index + 1);
            String host = endPointStr.split(":")[0];
            log.info("endPointStr:{}", endPointStr);
            int port = Integer.parseInt(endPointStr.split(":")[1]);
            list.add(new EndPoint(host, port));
        });
        return list;
    }
}
