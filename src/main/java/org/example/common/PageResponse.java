package org.example.common;


import org.apache.commons.io.IOUtils;
import org.example.exception.EngineException;
import org.example.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class PageResponse {
    public PageResponse(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }

    private PageRequest pageRequest;
    private String rawText;
    private int statusCode;
    private boolean success;
    private Parser parser;

    private String orgUrl;

    public String getOrgUrl() {
        return this.pageRequest.getUrl();
    }

    public <T> T getRequestExtra(String key) {
        return this.pageRequest.getExtra(key);
    }

    private Map<String, String> responseHeaders = new HashMap<>();


    private InputStream rawBody;

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void addResponseHeaders(String key,String value) {
        this.responseHeaders.put(key,value);
    }


    @Override
    public String toString() {
        return "PageResponse{" +
                "pageRequest=" + pageRequest +
                ", rawText='" + rawText + '\'' +
                ", statusCode=" + statusCode +
                ", success=" + success +
                ", spider=" + parser +
                ", rawBody=" + rawBody +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageResponse that = (PageResponse) o;
        return statusCode == that.statusCode && success == that.success && Objects.equals(pageRequest, that.pageRequest) && Objects.equals(rawText, that.rawText) && Objects.equals(parser, that.parser) && Objects.equals(rawBody, that.rawBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageRequest, rawText, statusCode, success, parser, rawBody);
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }

    public String getRawText() {
        if (!isSuccess()) {
            throw new EngineException("get raw text failed , response is failed");
        }
        return Objects.toString(rawText, "");
    }

    public String getRawTextFromBody() {
        if (!isSuccess()) {
            throw new EngineException("get raw text failed , response is failed");
        }
        try {
            if (rawText != null) {
                return rawText;
            }
            rawText = IOUtils.toString(this.rawBody, pageRequest.getCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Parser getSpider() {
        return parser;
    }

    public void setSpider(Parser parser) {
        this.parser = parser;
    }

    public InputStream getRawBody() {
        if (!isSuccess()) {
            throw new EngineException("get raw body failed ,response is failed");
        }
        return rawBody;
    }

    public void setRawBody(InputStream rawBody) {
        this.rawBody = rawBody;
    }

}
