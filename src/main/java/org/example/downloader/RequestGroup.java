package org.example.downloader;


import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.GroupQueueConfig;
import org.example.common.GroupStatus;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.engine.Spider;
import org.example.middleware.Middleware;
import org.example.middleware.MiddlewareChain;
import org.example.middleware.MiddlewareFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestGroup {
    private final String groupId;
    private final String groupName;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Logger logger = LoggerFactory.getLogger(RequestGroup.class);
    private final Spider spider;
    public volatile boolean running = true;
    private final int groupQueueSize;
    private final int poolSize;

    public RequestGroup(GroupQueueConfig config, Spider spider) {
        this.spider = spider;
        groupId = config.getQueueId();
        groupName = config.getQueueName();
        poolSize = config.getPoolSize();
        this.groupQueueSize = spider.getGroupQueueSize();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(groupQueueSize);
        String namingPattern = config.getQueueName() + "-pool-%d";
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern(namingPattern).build();
        this.threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 3000,
                TimeUnit.MILLISECONDS, workQueue, threadFactory, new BlockRejectedExecutionHandler());
    }

    public GroupStatus status() {
        GroupStatus groupStatus = new GroupStatus();
        Long completedTaskCount = this.threadPoolExecutor.getCompletedTaskCount();
        Long taskCount = this.threadPoolExecutor.getTaskCount();
        groupStatus.setGroupName(groupName);
        groupStatus.setGroupId(groupId);
        groupStatus.setPoolSize(poolSize);
        groupStatus.setWorkSize(groupQueueSize);
        groupStatus.setTaskCount(taskCount);
        groupStatus.setCompletedTaskCount(completedTaskCount);
        return groupStatus;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean updatePoolSize(Integer size) {
        if (size == null || size <= 0 || size > groupQueueSize) {
            logger.error("max size is : {}", groupQueueSize);
            return false;
        }
        this.threadPoolExecutor.setCorePoolSize(size);
        this.threadPoolExecutor.setMaximumPoolSize(size);

        return true;
    }

    public void addRequestTask(PageRequest pageRequest) {
        if (!running) {
            logger.error("queue : {} is not running", this.groupName);
        }
        if (pageRequest == null) {
            return;
        }
        PageResponse pageResponse = new PageResponse(pageRequest);
        List<Middleware> middlewares = pageRequest.getMiddlewares();
        MiddlewareChain middlewareChain = MiddlewareFactory.createMiddlewareChain(middlewares, spider);
        threadPoolExecutor.execute(() -> middlewareChain.doMiddleware(pageRequest, pageResponse));
    }

    public void destroy() {
        this.running = false;
        this.threadPoolExecutor.shutdown();
    }


}
