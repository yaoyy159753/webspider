package org.example.scheduler;


import org.example.common.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BaseScheduler implements Scheduler {
    private final ArrayBlockingQueue<PageRequest> blockingQueue = new ArrayBlockingQueue<>(10000);
    private final Set<String> urls = new HashSet<>();
    private final Logger logger = LoggerFactory.getLogger(BaseScheduler.class);

    @Override
    public void push(PageRequest request) {
        try {
            String url = request.getUrl();
            if (request.isNoFilter()) {
                blockingQueue.put(request);
                urls.add(url);
                return;
            }

            if (urls.contains(url)) {
                return;
            }
            blockingQueue.put(request);
            urls.add(url);
        } catch (Exception e) {
            logger.error("push failed", e);
        }
    }

    @Override
    public PageRequest poll() {
        try {
            return blockingQueue.poll(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("poll failed", e);
        }
        return null;
    }

    @Override
    public void destroy() {

    }
}
