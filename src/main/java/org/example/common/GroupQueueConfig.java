package org.example.common;

import org.example.util.CommonUtils;

import java.net.ProxySelector;

public class GroupQueueConfig {
    private String queueName;
    private String queueId;
    private ProxySelector proxySelector;
    /**
     * 同时进行任务
     */
    private Integer poolSize;

    public String getQueueName() {
        return queueName;
    }

    public static GroupQueueConfig simple(String queueName) {
        GroupQueueConfig config = new GroupQueueConfig();
        config.setQueueId(CommonUtils.uuid());
        config.setQueueName(queueName);
        config.setPoolSize(7);
        return config;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }


    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public ProxySelector getProxySelector() {
        return proxySelector;
    }

    public void setProxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }
}
