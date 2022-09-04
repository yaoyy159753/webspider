package org.example.common;


import org.example.config.RetryConfig;
import org.example.downloader.DownLoader;
import org.example.middleware.Middleware;

import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PageRequest {
    private final Map<String, Object> extra = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private Charset charset = StandardCharsets.UTF_8;
    private Proxy proxy;
    private String url;
    private String method;
    private String spiderName;
    private String group;
    private String downLoaderName;
    private DownLoader downLoader;
    private boolean noFilter;
    private List<String> beforeRequest = new ArrayList<>();
    private List<String> afterResponse = new ArrayList<>();
    private List<Middleware> requestMiddlewares = new ArrayList<>();
    private List<Middleware> responseMiddlewares = new ArrayList<>();
    private List<Middleware> middlewares = new ArrayList<>();
    private RetryConfig retryConfig = RetryConfig.custom();

    public List<Middleware> getMiddlewares() {
        return middlewares;
    }

    public void setMiddlewares(List<Middleware> middlewares) {
        this.middlewares = middlewares;
    }

    public static PageRequest url(String url) {
        PageRequest request = new PageRequest();
        request.setUrl(url);
        return request;
    }

    public static PageRequest url(String url, String spiderName) {
        PageRequest request = new PageRequest();
        request.setUrl(url);
        request.setSpiderName(spiderName);
        return request;
    }

    public PageRequest copy() {
        PageRequest request = new PageRequest();
        for (Map.Entry<String, Object> entry : extra.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            request.addExtra(k, v);
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            request.addHeader(k, v);
        }

        request.setCharset(this.charset);
        request.setMethod(this.method);
        request.setGroup(this.group);
        request.setDownLoaderName(this.downLoaderName);
        request.setSpiderName(this.spiderName);
        request.setNoFilter(this.noFilter);
        request.setBeforeRequest(new ArrayList<>(this.getBeforeRequest()));
        request.setAfterResponse(new ArrayList<>(this.getAfterResponse()));
        request.setMiddlewares(new ArrayList<>(this.getMiddlewares()));
        request.setRetryConfig(RetryConfig.custom());
        request.setProxy(this.proxy);
        return request;
    }

    public List<Middleware> getRequestMiddlewares() {
        return requestMiddlewares;
    }

    public void setRequestMiddlewares(List<Middleware> requestMiddlewares) {
        this.requestMiddlewares = requestMiddlewares;
    }

    public List<Middleware> getResponseMiddlewares() {
        return responseMiddlewares;
    }

    public void setResponseMiddlewares(List<Middleware> responseMiddlewares) {
        this.responseMiddlewares = responseMiddlewares;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }


    public List<String> getBeforeRequest() {
        return beforeRequest == null ? new ArrayList<>() : beforeRequest;
    }

    public void setBeforeRequest(List<String> beforeRequest) {
        this.beforeRequest = beforeRequest;
    }

    public List<String> getAfterResponse() {
        return afterResponse == null ? new ArrayList<>() : afterResponse;
    }

    public void setAfterResponse(List<String> afterResponse) {
        this.afterResponse = afterResponse;
    }

    public boolean isNoFilter() {
        return noFilter;
    }

    public void setNoFilter(boolean noFilter) {
        this.noFilter = noFilter;
    }

    public String getDownLoaderName() {
        return downLoaderName;
    }

    public void setDownLoaderName(String downLoaderName) {
        this.downLoaderName = downLoaderName;
    }

    public DownLoader getDownLoader() {
        return downLoader;
    }

    public void setDownLoader(DownLoader downLoader) {
        this.downLoader = downLoader;
    }


    // TODO 将配置区分为流程配置和请求配置
    public PageRequest() {
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(this.headers);
    }

    public Map<String, String> getParams() {
        return new HashMap<>(this.params);
    }

    public void addParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public boolean removeHeader(String key) {
        this.headers.remove(key);
        return this.headers.containsKey(key);
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public void setRetryConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public <T> void addExtra(String key, T extra) {
        this.extra.put(key, extra);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtra(String key) {
        return (T) this.extra.get(key);
    }


    @Override
    public String toString() {
        return "PageRequest{" +
                "spiderName='" + spiderName + '\'' +
                ", group='" + group + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageRequest that = (PageRequest) o;
        return Objects.equals(charset, that.charset) && Objects.equals(spiderName, that.spiderName) && Objects.equals(group, that.group) && Objects.equals(url, that.url) && Objects.equals(method, that.method) && Objects.equals(retryConfig, that.retryConfig) && Objects.equals(extra, that.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(charset, spiderName, group, url, method, retryConfig, extra);
    }

    public String getSpiderName() {
        return spiderName;
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}