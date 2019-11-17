package io.yzecho.rpcnettyetcd;

import io.yzecho.rpcnettyetcd.client.ClientMain;
import io.yzecho.rpcnettyetcd.client.proxy.ProxyFactory;
import io.yzecho.rpcnettyetcd.rpctest.common.TestService;
import io.yzecho.rpcnettyetcd.rpctest.client.TestServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yecho
 */
@SpringBootApplication
@RestController
public class RpcNettyEtcdApplication {

    private TestService testService;

    public RpcNettyEtcdApplication() {
        ClientMain clientMain = new ClientMain("io.yzecho.rpcnettyetcd.rpctest.client");
        clientMain.start();
        testService = ProxyFactory.create(TestServiceImpl.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RpcNettyEtcdApplication.class, args);
    }

    @GetMapping("/")
    public String sayHello() {
        return testService.sayHello();
    }
}
