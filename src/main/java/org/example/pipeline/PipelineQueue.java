package org.example.pipeline;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.PageItems;
import org.example.engine.Spider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PipelineQueue {
    private volatile boolean running = true;
    private final Logger logger = LoggerFactory.getLogger(PipelineQueue.class);
    private final ThreadPoolExecutor threadPoolExecutor;
    private int workQueueSize = Runtime.getRuntime().availableProcessors();
    private final int pipelineQueueSize;

    public PipelineQueue(Spider spider) {
        this.pipelineQueueSize = spider.getPipelineQueueSize();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(pipelineQueueSize);
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("pipeline-pool-%d").build();
        this.threadPoolExecutor = new ThreadPoolExecutor(workQueueSize, workQueueSize, 3000,
                TimeUnit.MILLISECONDS, workQueue, threadFactory, new BlockRejectedExecutionHandler());
    }

    public void addPipelineTask(PageItems pageItems) {
        if (pageItems == null) {
            return;
        }
        threadPoolExecutor.execute(() -> processAsync(pageItems));
    }

    public int getWorkQueueSize() {
        return this.workQueueSize;
    }

    public synchronized boolean updatePoolSize(Integer size) {
        if (size == null || size <= 0 || size > pipelineQueueSize) {
            logger.error("pipelineQueueSize max size is {}", pipelineQueueSize);
            return false;
        }
        this.threadPoolExecutor.setCorePoolSize(size);
        this.threadPoolExecutor.setMaximumPoolSize(size);
        this.workQueueSize = size;
        return true;
    }

    private void processAsync(PageItems pageItems) {
        Pipeline pipeline = pageItems.getPipeline();
        if (pipeline != null) {
            try {
                pipeline.process(pageItems);
            } catch (Throwable e) {
                try {
                    pipeline.onException(pageItems, e);
                } catch (Exception ex) {
                    logger.error("onException", ex);
                }
            }
        }
    }

    public void destroy() {
        this.running = false;
        threadPoolExecutor.shutdown();
    }

    public boolean isRunning() {
        return this.running;
    }
}

