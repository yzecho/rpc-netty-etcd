package io.yzecho.rpcnettyetcd.rpctest.server;

import io.yzecho.rpcnettyetcd.server.ServerMain;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 16:13
 */
public class ServerApplication {
    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain("io.yzecho.rpcnettyetcd.rpctest.server");
        serverMain.start();
    }
}
