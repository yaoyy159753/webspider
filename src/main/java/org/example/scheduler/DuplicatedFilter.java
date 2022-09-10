package org.example.scheduler;

import org.example.common.PageRequest;

public interface DuplicatedFilter {
    boolean seen(PageRequest pageRequest);
}
