package org.example.pipeline;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.PageItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PipelineQueue implements Runnable {
    private volatile boolean running = true;
    private final Logger logger = LoggerFactory.getLogger(PipelineQueue.class);
    private final ArrayBlockingQueue<PageItems> blockingQueue;
    private final ThreadPoolExecutor threadPoolExecutor;
    private int workQueueSize = Runtime.getRuntime().availableProcessors();

    public PipelineQueue() {
        this.blockingQueue = new ArrayBlockingQueue<>(1000);
        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("pipeline-pool-%d").build();
        this.threadPoolExecutor = new ThreadPoolExecutor(workQueueSize, workQueueSize, 3000,
                TimeUnit.MILLISECONDS, blockingQueue, threadFactory, new BlockRejectedExecutionHandler());
    }

    public void addTask(PageItems pageItems) {
        try {
            blockingQueue.put(pageItems);
        } catch (Exception e) {
            logger.error("addTask error", e);
        }
    }

    public int getWorkQueueSize() {
        return this.workQueueSize;
    }

    public synchronized boolean updatePoolSize(Integer size) {
        if (size == null || size <= 0) {
            return false;
        }
        this.threadPoolExecutor.setCorePoolSize(size);
        this.threadPoolExecutor.setMaximumPoolSize(size + 1);
        this.workQueueSize = size;
        return true;
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
                PageItems pageItems = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
                if (pageItems == null) {
                    continue;
                }
                threadPoolExecutor.execute(() -> processAsync(pageItems));
            } catch (Exception e) {
                logger.error("PipelineQueue error", e);
            }
        }
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

