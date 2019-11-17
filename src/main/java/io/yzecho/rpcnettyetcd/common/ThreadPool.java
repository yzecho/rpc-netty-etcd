package io.yzecho.rpcnettyetcd.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: yzecho
 * @desc
 * @date: 16/11/2019 09:10
 */
@Slf4j
public class ThreadPool {
    public static final ThreadPool INSTANCE = new ThreadPool();
    private final ThreadPoolExecutor executor;

    private ThreadPool() {
        // 核心线程数：10
        // 最大线程数：20
        // 线程保持活跃时间：60s
        // 队列：阻塞队列，最多存放100个任务
        // 拒绝策略：任务将被放弃
        this.executor = new ThreadPoolExecutor(10,
                20,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void submit(Runnable task) {
        executor.submit(task);
    }
}
