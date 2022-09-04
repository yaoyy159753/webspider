package org.example.middleware;

import org.example.common.PageRequest;
import org.example.common.PageResponse;

public interface MiddlewareChain {
    void doMiddleware(PageRequest pageRequest, PageResponse pageResponse);

    void thenRequest(PageRequest pageRequest);

    void thenResponse(PageResponse pageRequest);
}
