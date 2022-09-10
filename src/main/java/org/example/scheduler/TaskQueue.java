package org.example.scheduler;

import org.example.common.PageRequest;

public interface TaskQueue {
    boolean put(PageRequest pageRequest);

    PageRequest poll();
}
