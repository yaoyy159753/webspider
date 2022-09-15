package org.example.engine;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.example.common.BlockRejectedExecutionHandler;
import org.example.common.GroupQueueConfig;
import org.example.common.PageItems;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.downloader.DownLoader;
import org.example.downloader.OkClientDownLoader;
import org.example.downloader.RequestQueue;
import org.example.middleware.Middleware;
import org.example.middleware.base.DownloaderMiddleware;
import org.example.middleware.base.RedirectMiddleware;
import org.example.middleware.base.RetryMiddleware;
import org.example.parser.Parser;
import org.example.parser.ParserQueue;
import org.example.parser.base.SimpleParser;
import org.example.pipeline.Pipeline;
import org.example.pipeline.PipelineQueue;
import org.example.pipeline.base.SimplePipeline;
import org.example.scheduler.Scheduler;
import org.example.scheduler.SchedulerQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Spider {
    private volatile boolean running = true;
    private boolean redirect = true;
    private boolean retry = true;
    private final Map<String, Parser> parserMap = new LinkedHashMap<>();
    private final Map<String, Pipeline> pipelineMap = new LinkedHashMap<>();
    private final Map<String, DownLoader> downLoaderMap = new LinkedHashMap<>();
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Logger logger = LoggerFactory.getLogger(Spider.class);
    private Scheduler scheduler;
    private Integer workQueueSize = 2_0000;
    private final ParserQueue parserQueue;
    private final PipelineQueue pipelineQueue;
    private final RequestQueue requestQueue;
    private final SchedulerQueue schedulerQueue;
    private final Pipeline simplePipeline = new SimplePipeline();
    private final Parser simpleParser = new SimpleParser();
    private final DownLoader defaultDownloader = new OkClientDownLoader();
    private final Map<String, Middleware> beforeRequestMap = new HashMap<>();
    private final Map<String, Middleware> afterResponseMap = new HashMap<>();
    private final Middleware downloaderMiddleware = new DownloaderMiddleware();
    private final Middleware retryMiddleware = new RetryMiddleware();
    private final Middleware redirectMiddleware = new RedirectMiddleware();

    protected Spider() {
        logger.debug("spider start ....");
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("spider-pool-%d").build();
        BlockingQueue<Runnable> wordQueue = new LinkedBlockingQueue<>(100);
        this.threadPoolExecutor = new ThreadPoolExecutor(7, 7, 3000,
                TimeUnit.MILLISECONDS, wordQueue, threadFactory, new BlockRejectedExecutionHandler());
        this.parserQueue = new ParserQueue(this);
        this.pipelineQueue = new PipelineQueue();
        this.requestQueue = new RequestQueue(this);
        this.schedulerQueue = new SchedulerQueue(this);
    }

    protected void start() {
        this.threadPoolExecutor.execute(schedulerQueue);
        this.threadPoolExecutor.execute(parserQueue);
        this.threadPoolExecutor.execute(pipelineQueue);
    }

    protected void setWorkQueueSize(Integer workQueueSize) {
        this.workQueueSize = workQueueSize;
    }

    public Integer getWorkQueueSize() {
        return workQueueSize;
    }

    public static SpiderBuilder builder() {
        return new SpiderBuilder();
    }

    private boolean checkSpider() {
        return parserMap.isEmpty();
    }

    protected void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    protected void setRetry(boolean retry) {
        this.retry = retry;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public boolean isRetry() {
        return retry;
    }

    /**
     * 获取注册的spider名称列表
     *
     * @return 注册的spider名称列表
     */
    public Set<String> spiders() {
        return this.parserMap.keySet();
    }

    /**
     * 获取注册的requestListener名称列表
     *
     * @return 注册的requestListener名称列表
     */
    public Set<String> requestListeners() {
        return this.beforeRequestMap.keySet();
    }

    /**
     * 获取注册的responseListener名称列表
     *
     * @return 注册的responseListener名称列表
     */
    public Set<String> responseListeners() {
        return this.afterResponseMap.keySet();
    }

    /**
     * 获取分组下载配置列表
     *
     * @return 分组配置列表
     */
    public List<GroupQueueConfig> groupConfigs() {
        return this.requestQueue.getConfigs();
    }

    /**
     * 通过配置文件增加分组下载队列
     *
     * @param config 配置文件
     */
    public void createGroupQueueWithConfig(GroupQueueConfig config) {
        this.requestQueue.createGroupQueueWithConfig(config);
    }

    protected void registerScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void registerRequestMiddleware(String name, Middleware requestMiddleware) {
        this.beforeRequestMap.put(name, requestMiddleware);
    }

    public void removeRequestMiddleware(String name) {
        this.beforeRequestMap.remove(name);
    }

    public void registerResponseMiddleware(String name, Middleware requestMiddleware) {
        this.afterResponseMap.put(name, requestMiddleware);
    }

    public void removeResponseMiddleware(String name) {
        this.afterResponseMap.remove(name);
    }


    public void registerDownloader(DownLoader downLoader) {
        this.downLoaderMap.put(downLoader.getName(), downLoader);
    }

    public boolean removeDownloader(String key) {
        DownLoader remove = this.downLoaderMap.remove(key);
        return remove != null;
    }

    public void registerPipeline(Pipeline pipeline) {
        this.pipelineMap.put(pipeline.getName(), pipeline);
    }

    public boolean removePipeline(String key) {
        Pipeline remove = this.pipelineMap.remove(key);
        return remove != null;
    }

    public void registerParser(Parser parser) {
        this.parserMap.put(parser.getName(), parser);
    }

    public boolean removeParser(String key) {
        Parser remove = this.parserMap.remove(key);
        return remove != null;
    }
    public void addSimpleTask(String startUrl) {
       this.addTask(PageRequest.url(startUrl));
    }

    public void addTask(PageRequest request) {
        if (!running || request == null) {
            logger.error("spider is not running");
            return;
        }
        if (request.getDownLoader() == null) {
            DownLoader downLoader = selectDownloader(request.getDownLoaderName());
            request.setDownLoader(downLoader);
        }
        this.selectListener(request);
        this.scheduler.enqueue(request);
    }

    public void sendPipeline(PageItems items) {
        String pipelineName = items.getPipelineName();
        Pipeline pipeline = this.selectPipeline(pipelineName);
        items.setPipeline(pipeline);
        pipelineQueue.addTask(items);
    }

    private Pipeline selectPipeline(String pipelineName) {
        Pipeline pipeline = this.pipelineMap.get(pipelineName);
        if (pipeline == null) {
            logger.error("no pipelineName {} register , use simplePipeline", pipelineName);
            return this.simplePipeline;
        }

        return pipeline;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void createSpiderTask(PageRequest pageRequest) {
        try {
            requestQueue.createDownTask(pageRequest);
        } catch (Exception e) {
            logger.error("createDownTask error", e);
        }
    }

    public void responseCallback(PageResponse pageResponse) {
        this.toParser(pageResponse);
    }

    private void toParser(PageResponse pageResponse) {
        PageRequest pageRequest = pageResponse.getPageRequest();
        String spiderName = pageRequest.getSpiderName();
        Parser parser = this.selectParser(spiderName);
        pageResponse.setSpider(parser);
        parserQueue.addParseTask(pageResponse);
    }

    private Parser selectParser(String spiderName) {
        Parser parser = this.parserMap.get(spiderName);
        if (parser == null) {
            logger.error("no spiderName {} register , use simpleParser", spiderName);
            return this.simpleParser;
        }
        return parser;
    }

    private DownLoader selectDownloader(String key) {
        DownLoader downLoader = this.downLoaderMap.get(key);
        return downLoader == null ? this.defaultDownloader : downLoader;
    }

    private void selectListener(PageRequest pageRequest) {
        List<String> beforeRequest = pageRequest.getBeforeRequest();
        List<Middleware> tmp = beforeRequest.stream().map(this.beforeRequestMap::get).collect(Collectors.toList());
        tmp.add(downloaderMiddleware);
        List<String> afterResponse = pageRequest.getAfterResponse();
        List<Middleware> responseListeners = afterResponse.stream().map(this.afterResponseMap::get).collect(Collectors.toList());
        if (this.isRedirect()) {
            tmp.add(this.redirectMiddleware);
        }
        if (this.isRetry()) {
            tmp.add(this.retryMiddleware);
        }
        tmp.addAll(responseListeners);
        pageRequest.setMiddlewares(tmp);
    }

    public boolean removeGroupQueue(String queueName) {
        return this.requestQueue.removeGroupQueue(queueName);
    }

    public boolean updateGroupQueue(String queueName, Integer size) {
        return this.requestQueue.updateGroupQueue(queueName, size);
    }

    private void destroyRequestQueue() {
        if (this.requestQueue.isRunning()) {
            this.requestQueue.destroy();
        }
    }

    private void destroyPipelineQueue() {
        if (this.pipelineQueue.isRunning()) {
            this.pipelineQueue.destroy();
        }
    }

    private void destroySpiderQueue() {
        if (this.parserQueue.isRunning()) {
            this.parserQueue.destroy();
        }
    }

    private void destroySchedulerQueue() {
        if (this.schedulerQueue.isRunning()) {
            this.schedulerQueue.destroy();
        }
    }

    public void shutdown() {
        this.running = false;
        this.destroySchedulerQueue();
        this.destroyRequestQueue();
        this.destroySpiderQueue();
        this.destroyPipelineQueue();
        this.defaultDownloader.destroy();
        this.threadPoolExecutor.shutdown();
        if (this.threadPoolExecutor.isShutdown()) {
            logger.debug("shut down success....");
            System.out.println("shut down success....");
        }
        // TODO 任务尚未处理完成
    }

    public int getParserQueueWorkQueueSize() {
        return this.parserQueue.getWorkQueueSize();
    }

    public boolean updateParserQueueWorkQueueSize(int size) {
        return this.parserQueue.updatePoolSize(size);
    }

    public int getPipelineQueueWorkQueueSize() {
        return this.pipelineQueue.getWorkQueueSize();
    }

    public boolean updatePipelineQueueWorkQueueSize(int size) {
        return this.pipelineQueue.updatePoolSize(size);
    }

    public Set<String> downLoaders() {
        return this.downLoaderMap.keySet();
    }
}
