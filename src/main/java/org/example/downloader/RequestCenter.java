package org.example.downloader;

import org.example.common.GroupQueueConfig;
import org.example.common.PageRequest;
import org.example.engine.Spider;
import org.example.exception.SpiderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestCenter {
    private volatile boolean running = true;
    private final Map<String, RequestGroup> groupQueueMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(RequestCenter.class);
    private final Spider spider;

    public RequestCenter(Spider spider) {
        this.spider = spider;
    }

    public void createDownTask(PageRequest pageRequest) {
        if (!running) {
            logger.error("RequestQueue is not running");
            return;
        }
        RequestGroup requestGroup = this.selectGroup(pageRequest);
        if (requestGroup.isRunning()) {
            requestGroup.addRequestTask(pageRequest);
        } else {
            throw new SpiderException("groupQueue is not running");
        }

    }

    private RequestGroup selectGroup(PageRequest pageRequest) {
        String group = pageRequest.getGroup();
        String queueName = group == null || group.isEmpty() ? "default" : group;
        GroupQueueConfig simple = GroupQueueConfig.simple(queueName);
        createGroupQueueWithConfig(simple);
        return groupQueueMap.get(queueName);
    }

    public void createGroupQueueWithConfig(GroupQueueConfig config) {
        if (config == null) {
            logger.error("config is null ");
            return;
        }
        String queueName = config.getQueueName();
        if (groupQueueMap.containsKey(queueName)) {
            return;
        }
        RequestGroup requestGroup = new RequestGroup(config, spider);
        groupQueueMap.put(queueName, requestGroup);
    }

    /**
     * 根据下载分组名称，移除下载分组
     *
     * @param queueName 分组名称
     * @return 移除结果
     */
    public boolean removeGroupQueue(String queueName) {
        RequestGroup requestGroup = this.groupQueueMap.remove(queueName);
        if (requestGroup != null) {
            requestGroup.destroy();
            return true;
        }
        logger.error("no {} groupQueue found", queueName);
        return false;
    }

    /**
     * 根据下载分组名称，修改同时运行的任务数量
     *
     * @param queueName 分组名称
     * @param size      同时运行的任务数量
     * @return 返回结果
     */
    public boolean updateGroupQueue(String queueName, Integer size) {
        RequestGroup requestGroup = this.groupQueueMap.get(queueName);
        if (requestGroup != null) {
            return requestGroup.updatePoolSize(size);

        }
        logger.error("no {} groupQueue found , update failed", queueName);
        return false;
    }

    /**
     * 获取全部下载分组信息列表
     *
     * @return 分组信息列表
     */
    public List<GroupQueueConfig> getConfigs() {
        return groupQueueMap.values().stream().map(RequestGroup::getConfig).collect(Collectors.toList());
    }

    public void destroy() {
        this.running = false;
        for (Map.Entry<String, RequestGroup> entry : this.groupQueueMap.entrySet()) {
            RequestGroup requestGroup = entry.getValue();
            requestGroup.destroy();
        }
        this.groupQueueMap.clear();
    }

    public boolean isRunning() {
        return this.running;
    }
}
