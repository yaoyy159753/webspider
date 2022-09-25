package org.example.middleware.base;

import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.config.SiteConfig;
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
        SiteConfig siteConfig = pageRequest.getSiteConfig();
        if (siteConfig == null) {
            logger.error("no siteConfig found , request is {}", pageRequest);
            pageResponse.setSuccess(false);
            middlewareChain.thenResponse(pageResponse);
            return;
        }
        List<Integer> noRetryCode = siteConfig.getNoRetryCodes();
        if (noRetryCode.contains(statusCode)) {
            middlewareChain.doMiddleware(pageRequest, pageResponse);
            return;
        }

        if (siteConfig.checkRetryFlag()) {
            pageRequest.setNoFilter(true);
            siteConfig.retry();
            middlewareChain.thenRequest(pageRequest);
        } else {
            logger.error("retry time around max , request is {}", pageRequest);
            middlewareChain.thenResponse(pageResponse);
        }


    }
}
