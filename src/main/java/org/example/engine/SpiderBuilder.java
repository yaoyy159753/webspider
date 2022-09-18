package org.example.engine;

import org.example.downloader.DownLoader;
import org.example.middleware.Middleware;
import org.example.parser.Parser;
import org.example.pipeline.Pipeline;
import org.example.scheduler.DuplicatedFilter;
import org.example.scheduler.Scheduler;
import org.example.scheduler.TaskQueue;
import org.example.scheduler.base.LifoMemoryQueue;
import org.example.scheduler.base.SetDuplicatedFilter;

import java.util.HashMap;
import java.util.Map;

public class SpiderBuilder {
    protected SpiderBuilder() {
    }

    private TaskQueue taskQueue;
    private DuplicatedFilter duplicatedFilter;
    private boolean redirect = true;
    private boolean retry = true;
    private int parserQueueSize;
    private int pipelineQueueSize;
    private int groupQueueSize;
    private final Map<String, Parser> spiderMap = new HashMap<>();
    private final Map<String, Pipeline> pipelineMap = new HashMap<>();
    private final Map<String, DownLoader> downLoaderMap = new HashMap<>();
    private final Map<String, Middleware> beforeRequestMap = new HashMap<>();
    private final Map<String, Middleware> afterResponseMap = new HashMap<>();

    public SpiderBuilder addParser(Parser parser) {
        this.spiderMap.put(parser.getName(), parser);
        return this;
    }

    public SpiderBuilder groupQueueSize(int groupQueueSize) {
        this.groupQueueSize = groupQueueSize;
        return this;
    }

    public SpiderBuilder parserQueueSize(int parserQueueSize) {
        this.parserQueueSize = parserQueueSize;
        return this;
    }

    public SpiderBuilder pipelineQueueSize(int pipelineQueueSize) {
        this.pipelineQueueSize = pipelineQueueSize;
        return this;
    }

    public SpiderBuilder requestMiddleware(String name, Middleware requestMiddleware) {
        this.beforeRequestMap.put(name, requestMiddleware);
        return this;
    }

    public SpiderBuilder responseMiddleware(String name, Middleware requestMiddleware) {
        this.afterResponseMap.put(name, requestMiddleware);
        return this;
    }


    public SpiderBuilder addPipeline(Pipeline pipeline) {
        this.pipelineMap.put(pipeline.getName(), pipeline);
        return this;
    }

    public SpiderBuilder addDownLoader(DownLoader downLoader) {
        this.downLoaderMap.put(downLoader.getName(), downLoader);
        return this;
    }

    public SpiderBuilder autoRedirect(boolean autoRedirect) {
        this.redirect = autoRedirect;
        return this;
    }

    public SpiderBuilder retryOnFail(boolean retryOnFail) {
        this.retry = retryOnFail;
        return this;
    }

    public SpiderBuilder taskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
        return this;
    }

    public SpiderBuilder duplicatedFilter(DuplicatedFilter duplicatedFilter) {
        this.duplicatedFilter = duplicatedFilter;
        return this;
    }

    public Spider start() {
        Spider spider = new Spider();
        for (Map.Entry<String, Parser> entry : spiderMap.entrySet()) {
            Parser v = entry.getValue();
            spider.registerParser(v);
        }
        for (Map.Entry<String, Pipeline> entry : pipelineMap.entrySet()) {
            Pipeline v = entry.getValue();
            spider.registerPipeline(v);
        }
        for (Map.Entry<String, DownLoader> entry : this.downLoaderMap.entrySet()) {
            DownLoader v = entry.getValue();
            spider.registerDownloader(v);
        }

        for (Map.Entry<String, Middleware> entry : this.beforeRequestMap.entrySet()) {
            String name = entry.getKey();
            Middleware v = entry.getValue();
            spider.registerRequestMiddleware(name, v);
        }

        for (Map.Entry<String, Middleware> entry : this.afterResponseMap.entrySet()) {
            String name = entry.getKey();
            Middleware v = entry.getValue();
            spider.registerResponseMiddleware(name, v);
        }

        if (taskQueue == null) {
            taskQueue = new LifoMemoryQueue();
        }
        if (duplicatedFilter == null) {
            duplicatedFilter = new SetDuplicatedFilter();
        }
        spider.registerScheduler(new Scheduler(taskQueue, duplicatedFilter));

        if (this.parserQueueSize > 0) {
            spider.setParserQueueSize(this.parserQueueSize);
        }
        if (this.pipelineQueueSize > 0) {
            spider.setPipelineQueueSize(this.pipelineQueueSize);
        }

        if (this.groupQueueSize > 0) {
            spider.setGroupQueueSize(this.groupQueueSize);
        }

        spider.setRedirect(this.redirect);
        spider.setRetry(this.retry);
        spider.start();
        return spider;
    }
}
