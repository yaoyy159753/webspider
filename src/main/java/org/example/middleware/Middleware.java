package org.example.middleware;

import org.example.common.PageRequest;
import org.example.common.PageResponse;

public interface Middleware{
    void doMiddleware(PageRequest request, PageResponse pageResponse, MiddlewareChain middlewareChain);
}
