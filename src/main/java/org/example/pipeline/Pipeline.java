package org.example.pipeline;

import org.example.common.PageItems;

public abstract class Pipeline {
    private final String name;

    public Pipeline(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract void process(PageItems pageItems);

    public abstract void onException(PageItems pageItems, Throwable throwable);
}
