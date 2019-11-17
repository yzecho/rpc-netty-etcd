package io.yzecho.rpcnettyetcd;

import com.google.protobuf.ByteString;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.yzecho.rpcnettyetcd.common.etcd.EndPoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class RpcNettyEtcdApplicationTests {

    @Test
    void EtcdTest() throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient();

        String strKey = MessageFormat.format("/{0}", "test");
        ByteSequence key = ByteSequence.from(ByteString.copyFromUtf8(strKey));
        // 目前只需要创建这个key，对应的vaule暂不使用，先留空
        ByteSequence value = ByteSequence.from(ByteString.copyFromUtf8(""));
        // 等待put结束之后继续执行
        // kvClient.put(key, value).get();

        ByteSequence key2 = ByteSequence.from(ByteString.copyFromUtf8(strKey));
        GetResponse response = kvClient.get(key2, GetOption.newBuilder().withPrefix(key2).build()).get();
        //            String s = keyValue.getKey().toString(StandardCharsets.UTF_8);
        //            int index = s.lastIndexOf("/");
        //            String endPointStr = s.substring(index + 1, s.length());
        //            String host = endPointStr.split(":")[0];
        //            int port = Integer.parseInt(endPointStr.split(":")[1]);
        //            list.add(new EndPoint(host, port));
        response.getKvs().forEach(keyValue -> {
            String s = keyValue.getValue().toString(StandardCharsets.UTF_8);

            int index = s.lastIndexOf("/");
            String endPointStr = s.substring(index + 1, s.length());
            String host = endPointStr.split(":")[0];
            int port = Integer.parseInt(endPointStr.split(":")[1]);
            System.out.println(s);
            System.out.println(index);
            System.out.println(endPointStr);
            System.out.println(host);
            System.out.println(port);
        });
    }

}
