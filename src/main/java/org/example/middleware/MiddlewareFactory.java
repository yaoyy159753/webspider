package org.example.middleware;

import org.example.engine.Spider;

import java.util.List;

public class MiddlewareFactory {


    public static MiddlewareChain createMiddlewareChain(List<Middleware> middlewares, Spider spider) {
        DefaultMiddlewareChain requestMiddlewareChain = new DefaultMiddlewareChain();
        requestMiddlewareChain.setSpider(spider);
        requestMiddlewareChain.addMiddlewares(middlewares);
        return requestMiddlewareChain;
    }

}
