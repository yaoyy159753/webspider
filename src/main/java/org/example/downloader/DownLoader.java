package org.example.downloader;

import org.example.common.PageRequest;
import org.example.common.PageResponse;

public abstract class DownLoader {
    private final String name;

    public DownLoader(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract void request(PageRequest request, PageResponse pageResponse);

    public abstract void destroy();
}
