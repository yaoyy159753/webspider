package org.example.middleware.base;

import org.apache.commons.lang3.ArrayUtils;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.middleware.Middleware;
import org.example.middleware.MiddlewareChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RedirectMiddleware implements Middleware {
    private final Logger logger = LoggerFactory.getLogger(RedirectMiddleware.class);
    private final int[] redirectCodes = new int[]{301, 302, 303, 307, 308};

    @Override
    public void doMiddleware(PageRequest pageRequest, PageResponse pageResponse, MiddlewareChain middlewareChain) {
        int statusCode = pageResponse.getStatusCode();
        if (!ArrayUtils.contains(redirectCodes, statusCode)) {
            middlewareChain.doMiddleware(pageRequest, pageResponse);
            return;
        }

        Map<String, String> responseHeaders = pageResponse.getResponseHeaders();
        String location = responseHeaders.get("location");
        if (location == null) {
            location = responseHeaders.get("Location");
        }
        if (location != null) {
            PageRequest copy = pageRequest.copy();
            copy.setUrl(location.trim());
            middlewareChain.thenRequest(copy);
            return;
        }
        middlewareChain.doMiddleware(pageRequest, pageResponse);
    }
}
