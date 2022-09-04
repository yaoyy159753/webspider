package org.example.middleware.base;

import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.config.RetryConfig;
import org.example.middleware.Middleware;
import org.example.middleware.MiddlewareChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RetryMiddleware implements Middleware {
    private final Logger logger = LoggerFactory.getLogger(RetryMiddleware.class);

    @Override
    public void doMiddleware(PageRequest pageRequest, PageResponse pageResponse, MiddlewareChain middlewareChain) {
        int statusCode = pageResponse.getStatusCode();
        RetryConfig retryConfig = pageRequest.getRetryConfig();
        if (retryConfig == null) {
            logger.error("no siteConfig found , request is {}", pageRequest);
            pageResponse.setSuccess(false);
            middlewareChain.thenResponse(pageResponse);
            return;
        }
        List<Integer> noRetryCode = retryConfig.getNoRetryCodes();
        if (noRetryCode.contains(statusCode)) {
            middlewareChain.doMiddleware(pageRequest, pageResponse);
            return;
        }

        if (retryConfig.checkRetryFlag()) {
            pageRequest.setNoFilter(true);
            retryConfig.retry();
            middlewareChain.thenRequest(pageRequest);
        } else {
            logger.error("retry time around max , request is {}", pageRequest);
            middlewareChain.thenResponse(pageResponse);
        }


    }
}
