package org.example.scheduler.base;

import org.example.common.PageRequest;
import org.example.scheduler.TaskQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LifoMemoryQueue implements TaskQueue {
    private final ArrayBlockingQueue<PageRequest> blockingQueue = new ArrayBlockingQueue<>(10000);

    @Override
    public boolean put(PageRequest pageRequest) {
        try {
            blockingQueue.put(pageRequest);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public PageRequest poll() {
        try {
            return blockingQueue.poll(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
