package org.example.downloader;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.GroupQueueConfig;
import org.example.common.PageRequest;
import org.example.engine.Spider;
import org.example.exception.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RequestQueue {
    private volatile boolean running = true;
    private int workQueueSize = 666;
    private final Map<String, GroupQueue> groupQueueMap = new HashMap<>();
    private final BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("request-pool-%d").build();
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(workQueueSize, workQueueSize, 3000,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), threadFactory, new BlockRejectedExecutionHandler());
    private final Logger logger = LoggerFactory.getLogger(RequestQueue.class);
    private final Spider spider;

    public RequestQueue(Spider spider) {
        this.spider = spider;
    }

    public int getWorkQueueSize() {
        return workQueueSize;
    }

    public void createDownTask(PageRequest pageRequest) throws InterruptedException {
        if (!running) {
            logger.error("RequestQueue is not running");
            return;
        }
        GroupQueue groupQueue = this.selectQueue(pageRequest);
        if (groupQueue.isRunning()) {
            groupQueue.addTask(pageRequest);
        } else {
            throw new EngineException("groupQueue is not running");
        }

    }

    private GroupQueue selectQueue(PageRequest pageRequest) {
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
        ArrayBlockingQueue<PageRequest> queue = new ArrayBlockingQueue<>(spider.getWorkQueueSize());
        GroupQueue groupQueue = new GroupQueue(config);
        groupQueue.setEngine(spider);
        groupQueue.setBlockingQueue(queue);
        groupQueueMap.put(queueName, groupQueue);
        threadPoolExecutor.execute(groupQueue);
    }

    /**
     * 根据下载分组名称，移除下载分组
     *
     * @param queueName 分组名称
     * @return 移除结果
     */
    public boolean removeGroupQueue(String queueName) {
        GroupQueue groupQueue = this.groupQueueMap.remove(queueName);
        if (groupQueue != null) {
            groupQueue.destroy();
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
        GroupQueue groupQueue = this.groupQueueMap.get(queueName);
        if (groupQueue != null) {
            return groupQueue.updatePoolSize(size);

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
        return groupQueueMap.values().stream().map(GroupQueue::getConfig).collect(Collectors.toList());
    }


    public void destroy() {
        this.running = false;
        for (Map.Entry<String, GroupQueue> entry : this.groupQueueMap.entrySet()) {
            GroupQueue groupQueue = entry.getValue();
            groupQueue.destroy();
        }
        this.groupQueueMap.clear();
        this.threadPoolExecutor.shutdown();
    }

    public boolean isRunning() {
        return this.running;
    }
}
