package org.example.scheduler;

import org.example.common.PageRequest;

public interface Scheduler {


    void push(PageRequest request);

    PageRequest poll();

    void destroy();

}
