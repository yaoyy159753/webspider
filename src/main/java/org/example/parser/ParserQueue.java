package org.example.parser;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.*;
import org.example.engine.Spider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ParserQueue implements Runnable {
    private volatile boolean running = true;
    private final Logger logger = LoggerFactory.getLogger(ParserQueue.class);
    private final ArrayBlockingQueue<PageResponse> blockingQueue;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Spider spider;
    private int workQueueSize = Runtime.getRuntime().availableProcessors();

    public ParserQueue(Spider spider) {
        this.blockingQueue = new ArrayBlockingQueue<>(1000);
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("parser-pool-%d").build();
        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);
        this.threadPoolExecutor = new ThreadPoolExecutor(workQueueSize, workQueueSize, 3000,
                TimeUnit.MILLISECONDS, blockingQueue, threadFactory, new BlockRejectedExecutionHandler());
        this.spider = spider;
    }

    public void addParseTask(PageResponse response) {
        try {
            blockingQueue.put(response);
        } catch (InterruptedException e) {
            logger.error("addParseTask error", e);
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
                PageResponse take = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
                if (take == null) {
                    continue;
                }
                threadPoolExecutor.execute(() -> processAsync(take));
            } catch (Exception e) {
                logger.error("spiderQueue failed", e);
            }
        }
    }

    private void processAsync(PageResponse pageResponse) {
        ProcessResult processResult = new ProcessResult(pageResponse);
        Parser parser = pageResponse.getSpider();
        if (parser != null) {
            try {
                parser.process(pageResponse, processResult);
            } catch (Throwable throwable) {
                try {
                    parser.onException(pageResponse, throwable);
                } catch (Exception e) {
                    logger.error("onException", e);
                }
            }
        }
        List<PageRequest> pageRequests = processResult.getPageRequests();
        for (PageRequest pageRequest : pageRequests) {
            spider.addTask(pageRequest);
        }
        List<PageItems> pageItems = processResult.getPageItems();
        for (PageItems pageItem : pageItems) {
            spider.sendPipeline(pageItem);
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
