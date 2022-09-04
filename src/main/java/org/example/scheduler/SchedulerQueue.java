package org.example.scheduler;

import org.example.common.PageRequest;
import org.example.engine.Spider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerQueue implements Runnable {
    private volatile boolean running = true;
    private final Logger logger = LoggerFactory.getLogger(SchedulerQueue.class);
    private final Spider spider;

    public SchedulerQueue(Spider spider) {
        this.spider = spider;
    }

    @Override
    public void run() {
        Scheduler scheduler = spider.getScheduler();
        while (true) {
            try {
                if (!running) {
                    scheduler.destroy();
                    break;
                }
                PageRequest pageRequest = scheduler.poll();
                if (pageRequest == null) {
                    logger.debug("no pageRequest,sleep...");
                    // 如果scheduler没有使用阻塞队列，会出现性能问题
                    Thread.sleep(1000);
                    continue;
                }
                spider.createSpiderTask(pageRequest);
            } catch (Exception e) {
                logger.error("sendDownLoader error", e);
            }
        }
    }

    public void destroy() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }
}
