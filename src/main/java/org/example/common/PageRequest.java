package org.example.common;


import org.example.config.SiteConfig;
import org.example.downloader.DownLoader;
import org.example.middleware.Middleware;

import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PageRequest {
    private final Map<String, Object> extra = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private Charset charset = StandardCharsets.UTF_8;
    private boolean mobileMode = false;
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
    private SiteConfig siteConfig = SiteConfig.custom();

    public List<Middleware> getMiddlewares() {
        return middlewares;
    }

    public boolean isMobileMode() {
        return mobileMode;
    }

    public void setMobileMode(boolean mobileMode) {
        this.mobileMode = mobileMode;
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
        request.setMobileMode(this.isMobileMode());
        request.setBeforeRequest(new ArrayList<>(this.getBeforeRequest()));
        request.setAfterResponse(new ArrayList<>(this.getAfterResponse()));
        request.setMiddlewares(new ArrayList<>(this.getMiddlewares()));
        request.setSiteConfig(SiteConfig.custom());
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

    public SiteConfig getSiteConfig() {
        return siteConfig;
    }

    public void setSiteConfig(SiteConfig siteConfig) {
        this.siteConfig = siteConfig;
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
        return Objects.equals(charset, that.charset) && Objects.equals(spiderName, that.spiderName) && Objects.equals(group, that.group) && Objects.equals(url, that.url) && Objects.equals(method, that.method) && Objects.equals(siteConfig, that.siteConfig) && Objects.equals(extra, that.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(charset, spiderName, group, url, method, siteConfig, extra);
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
