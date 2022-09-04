package org.example.common;

import org.example.pipeline.Pipeline;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PageItems {

    private final Map<String, Object> items = new HashMap<>();
    private String pipelineName;
    private Pipeline pipeline;

    private PageItems() {
    }

    public static PageItems byName(String pipelineName) {
        PageItems pageItems = new PageItems();
        pageItems.setPipelineName(pipelineName);
        return pageItems;
    }

    public static PageItems simple() {
        return new PageItems();
    }

    public <T> PageItems item(String key, T value) {
        this.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object o = items.get(key);
        if (o == null) {
            return null;
        }
        return (T) items.get(key);
    }

    public Map<String, Object> items() {
        return new LinkedHashMap<>(this.items);
    }

    public <T> void put(String key, T value) {
        items.put(key, value);
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageItems pageItems = (PageItems) o;
        return Objects.equals(items, pageItems.items) && Objects.equals(pipelineName, pageItems.pipelineName) && Objects.equals(pipeline, pageItems.pipeline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, pipelineName, pipeline);
    }

    @Override
    public String toString() {
        return "PageItems{" +
                "items=" + items +
                ", pipelineName='" + pipelineName + '\'' +
                ", pipeline=" + pipeline +
                '}';
    }
}
