package org.example.parser;

import org.example.common.PageResponse;
import org.example.common.ProcessResult;

public abstract class Parser {
    private final String name;

    public Parser(String name) {
        this.name = name;
    }

    public abstract void process(PageResponse pageResponse,ProcessResult processResult);

    public abstract void onException(PageResponse pageResponse,Throwable throwable);

    public String getName() {
        return this.name;
    }

}
