package org.example.pipeline.base;

import org.example.common.PageItems;
import org.example.parser.base.SimpleParser;
import org.example.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class SimplePipeline extends Pipeline {
    private final Logger logger = LoggerFactory.getLogger(SimpleParser.class);

    public SimplePipeline() {
        super("simplePipeline");
    }

    @Override
    public void process(PageItems pageItems) {
        Map<String, Object> items = pageItems.items();
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            System.out.println(k + "=" + Objects.toString(v, ""));
        }
    }

    @Override
    public void onException(PageItems pageItems, Throwable throwable) {
        logger.error("error in Pipeline", throwable);
    }
}
