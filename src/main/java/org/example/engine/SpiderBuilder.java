package org.example.engine;

import org.example.downloader.DownLoader;
import org.example.middleware.Middleware;
import org.example.parser.Parser;
import org.example.pipeline.Pipeline;
import org.example.scheduler.BaseScheduler;
import org.example.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

public class SpiderBuilder {
    protected SpiderBuilder() {
    }

    private Integer workQueueSize;
    private Scheduler scheduler;
    private boolean redirect = true;
    private boolean retry = true;
    private final Map<String, Parser> spiderMap = new HashMap<>();
    private final Map<String, Pipeline> pipelineMap = new HashMap<>();
    private final Map<String, DownLoader> downLoaderMap = new HashMap<>();
    private final Map<String, Middleware> beforeRequestMap = new HashMap<>();
    private final Map<String, Middleware> afterResponseMap = new HashMap<>();

    public SpiderBuilder scheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public SpiderBuilder addParser(Parser parser) {
        this.spiderMap.put(parser.getName(), parser);
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

    public SpiderBuilder workQueueSize(Integer workQueueSize) {
        this.workQueueSize = workQueueSize;
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

        if (scheduler != null) {
            spider.registerScheduler(scheduler);
        } else {
            spider.registerScheduler(new BaseScheduler());
        }
        if (workQueueSize != null && workQueueSize > 0) {
            spider.setWorkQueueSize(workQueueSize);
        }
        spider.setRedirect(this.redirect);
        spider.setRetry(this.retry);
        spider.start();
        return spider;
    }
}
