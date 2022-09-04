package org.example.middleware;

import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.engine.Spider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class DefaultMiddlewareChain implements MiddlewareChain {
    private final Logger logger = LoggerFactory.getLogger(DefaultMiddlewareChain.class);
    List<Middleware> list = new ArrayList<>();
    private Spider spider;

    void setSpider(Spider spider) {
        this.spider = spider;
    }

    DefaultMiddlewareChain() {
    }

    private int cur;

    private int al;

    private boolean interrupt = false;

    @Override
    public void doMiddleware(PageRequest pageRequest, PageResponse pageResponse) {
        if (interrupt) {
            return;
        }
        if (cur < al) {
            Middleware middleware = list.get(cur++);
            try {
                middleware.doMiddleware(pageRequest, pageResponse, this);
            } catch (Exception e) {
                this.spider.responseCallback(pageResponse);
            }
            return;
        }
        this.spider.responseCallback(pageResponse);
    }

    @Override
    public void thenRequest(PageRequest pageRequest) {
        this.interruptChain();
        this.toSpider(pageRequest);
    }

    @Override
    public void thenResponse(PageResponse pageResponse) {
        this.interruptChain();
        this.spider.responseCallback(pageResponse);
    }

    void interruptChain() {
        this.interrupt = true;
    }

    void toSpider(PageRequest request) {
        this.spider.addTask(request);
    }

    void addMiddlewares(List<Middleware> middlewares) {
        this.list.addAll(middlewares);
        this.cur = 0;
        this.al = this.list.size();
    }


}
