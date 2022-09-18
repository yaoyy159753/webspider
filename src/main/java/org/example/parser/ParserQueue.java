package org.example.parser;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.PageItems;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.common.ProcessResult;
import org.example.engine.Spider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ParserQueue {
    private volatile boolean running = true;
    private final Logger logger = LoggerFactory.getLogger(ParserQueue.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    private final Spider spider;
    private int workQueueSize = Runtime.getRuntime().availableProcessors();
    private final int parserQueueSize;

    public ParserQueue(Spider spider) {
        this.spider = spider;
        this.parserQueueSize = spider.getParserQueueSize();
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("parser-pool-%d").build();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(parserQueueSize);
        this.threadPoolExecutor = new ThreadPoolExecutor(workQueueSize, workQueueSize, 3000,
                TimeUnit.MILLISECONDS, workQueue, threadFactory, new BlockRejectedExecutionHandler());
    }

    public void addParseTask(PageResponse response) {
        if (response == null) {
            return;
        }
        threadPoolExecutor.execute(() -> processAsync(response));
    }

    public int getWorkQueueSize() {
        return this.workQueueSize;
    }

    public synchronized boolean updatePoolSize(Integer size) {
        if (size == null || size <= 0 || size > parserQueueSize) {
            logger.error("parserQueueSize max size is {}", parserQueueSize);
            return false;
        }
        this.threadPoolExecutor.setCorePoolSize(size);
        this.threadPoolExecutor.setMaximumPoolSize(size);
        this.workQueueSize = size;
        return true;
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
                } catch (Throwable e) {
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
