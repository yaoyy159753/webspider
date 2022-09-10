package org.example.scheduler.base;

import org.example.common.PageRequest;
import org.example.scheduler.DuplicatedFilter;

import java.util.HashSet;
import java.util.Set;

public class SetDuplicatedFilter implements DuplicatedFilter {
    private final Set<String> set;

    public SetDuplicatedFilter() {
        set = new HashSet<>();
    }

    public SetDuplicatedFilter(Set<String> set) {
        this.set = set;
    }

    @Override
    public boolean seen(PageRequest pageRequest) {
        if (pageRequest == null) {
            return false;
        }
        String url = pageRequest.getUrl();
        if (set.contains(url)) {
            return true;
        } else {
            set.add(url);
            return false;
        }

    }
}
