package org.freshmarker;

import java.util.concurrent.atomic.AtomicInteger;

public record ReductionStatus(AtomicInteger total, AtomicInteger deleted, AtomicInteger changed) {
    public ReductionStatus() {
        this(new AtomicInteger(), new AtomicInteger(), new AtomicInteger());
    }
}
