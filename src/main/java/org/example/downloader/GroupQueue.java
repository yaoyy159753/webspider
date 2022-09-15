package org.example.downloader;


import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.GroupQueueConfig;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.engine.Spider;
import org.example.middleware.Middleware;
import org.example.middleware.MiddlewareChain;
import org.example.middleware.MiddlewareFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GroupQueue implements Runnable {
    private final GroupQueueConfig config;
    private BlockingQueue<PageRequest> blockingQueue;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Logger logger = LoggerFactory.getLogger(GroupQueue.class);
    private Spider spider;
    public volatile boolean running = true;
    // TODO 暂定为linux的最大端口数量。不知道是否合理
    private final Integer maxSize = 65535;

    public GroupQueue(GroupQueueConfig config) {
        this.config = config;
        Integer poolSize = config.getPoolSize();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(maxSize);
        String namingPattern = config.getQueueName() + "-pool-%d";
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern(namingPattern).build();
        this.threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize + 1, 3000,
                TimeUnit.MILLISECONDS, workQueue, threadFactory, new BlockRejectedExecutionHandler());
    }

    public void setEngine(Spider spider) {
        this.spider = spider;
    }

    public void setBlockingQueue(BlockingQueue<PageRequest> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public GroupQueueConfig getConfig() {
        return config;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean updatePoolSize(Integer size) {
        if (size == null || size <= 0 || size > maxSize) {
            return false;
        }
        this.threadPoolExecutor.setCorePoolSize(size);
        this.threadPoolExecutor.setMaximumPoolSize(size + 1);
        this.config.setPoolSize(size);
        return true;
    }

    public void addTask(PageRequest request) throws InterruptedException {
        if (!running) {
            logger.error("queue : {} is not running", this.config.getQueueName());
            return;
        }
        this.blockingQueue.put(request);
    }

    public void destroy() {
        this.running = false;
        this.threadPoolExecutor.shutdown();
    }


    @Override
    public void run() {
        while (true) {
            try {
                if (blockingQueue.size() == 0 && !this.running) {
                    // 销毁线程池，并关闭主线程
                    this.threadPoolExecutor.shutdown();
                    break;
                }
                PageRequest pageRequest = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
                if (pageRequest == null) {
                    continue;
                }
                PageResponse pageResponse = new PageResponse(pageRequest);
                List<Middleware> middlewares = pageRequest.getMiddlewares();
                MiddlewareChain middlewareChain = MiddlewareFactory.createMiddlewareChain(middlewares, spider);
                threadPoolExecutor.execute(() -> {
                    middlewareChain.doMiddleware(pageRequest, pageResponse);
                });

            } catch (Exception e) {
                logger.error("group down error", e);
            }
        }

    }
}
