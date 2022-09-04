package org.example.common;

import java.util.ArrayList;
import java.util.List;

public class ProcessResult {
    private PageRequest pageRequest;
    private PageResponse pageResponse;

    public ProcessResult(PageResponse pageResponse) {
        this.pageRequest = pageResponse.getPageRequest();
        this.pageResponse = pageResponse;
    }

    private final List<PageRequest> pageRequests = new ArrayList<>();

    private final List<PageItems> pageItems = new ArrayList<>();

    public void add(PageRequest request) {
        this.pageRequests.add(request);
    }

    public void add(PageItems pageItem) {
        this.pageItems.add(pageItem);
    }

    public PageRequest buildNextRequest(String url, String spiderName) {
        return nextRequest(url, spiderName);
    }

    public List<PageRequest> getPageRequests() {
        return new ArrayList<>(this.pageRequests);
    }

    public List<PageItems> getPageItems() {
        return new ArrayList<>(this.pageItems);
    }

    private PageRequest nextRequest(String url, String spiderName) {
        PageRequest copy = this.pageRequest.copy();
        copy.setUrl(url);
        copy.setSpiderName(spiderName);
        return copy;
    }


}
