package org.example.parser.base;

import org.example.common.PageItems;
import org.example.common.PageResponse;
import org.example.common.ProcessResult;
import org.example.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleParser extends Parser {
    private final Logger logger = LoggerFactory.getLogger(SimpleParser.class);

    public SimpleParser() {
        super("simpleParser");
    }

    @Override
    public void process(PageResponse pageResponse, ProcessResult processResult) {
        String rawText = pageResponse.getRawTextFromBody();
        processResult.add(PageItems.simple().item("rawtext", rawText));
    }

    @Override
    public void onException(PageResponse pageResponse, Throwable throwable) {
        logger.error("error in Parser", throwable);
    }
}
