package org.example.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class BlockRejectedExecutionHandler implements RejectedExecutionHandler {
    private final Logger logger = LoggerFactory.getLogger(BlockRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                // should not be interrupted
                logger.error("InterruptedException", e);
            }
        }
    }
}
