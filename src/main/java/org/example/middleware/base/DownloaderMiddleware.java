package org.example.middleware.base;

import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.downloader.DownLoader;
import org.example.middleware.Middleware;
import org.example.middleware.MiddlewareChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloaderMiddleware implements Middleware {
    private final Logger logger = LoggerFactory.getLogger(DownloaderMiddleware.class);
    @Override
    public void doMiddleware(PageRequest pageRequest, PageResponse pageResponse, MiddlewareChain middlewareChain) {
        DownLoader downLoader = pageRequest.getDownLoader();
        if (downLoader == null) {
            logger.error("no downLoader found");
            middlewareChain.doMiddleware(pageRequest, pageResponse);
            return;
        }
        downLoader.request(pageRequest, pageResponse);
        middlewareChain.doMiddleware(pageRequest, pageResponse);
    }
}
