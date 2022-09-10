package org.example.scheduler;

import org.example.common.PageRequest;

public class Scheduler {
    private final TaskQueue taskQueue;
    private final DuplicatedFilter duplicatedFilter;
    public Scheduler(TaskQueue taskQueue, DuplicatedFilter duplicatedFilter) {
        this.taskQueue = taskQueue;
        this.duplicatedFilter = duplicatedFilter;
    }

    public void enqueue(PageRequest request) {
        if (request.isNoFilter()) {
            taskQueue.put(request);
            return;
        }
        if (duplicatedFilter.seen(request)) {
            return;
        }
        taskQueue.put(request);
    }


    public PageRequest next() {
        return taskQueue.poll();
    }
}
