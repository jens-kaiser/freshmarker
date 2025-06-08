package org.freshmarker;

import java.util.concurrent.atomic.AtomicInteger;

public record ReductionStatus(AtomicInteger before, AtomicInteger after, AtomicInteger replaced) {
    public ReductionStatus() {
        this(new AtomicInteger(), new AtomicInteger(), new AtomicInteger());
    }

    public ReductionStatus(int before, int after) {
        this(new AtomicInteger(before), new AtomicInteger(after), new AtomicInteger());
    }

    public ReductionStatus(int before, int after, int replaced) {
        this(new AtomicInteger(before), new AtomicInteger(after), new AtomicInteger(replaced));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReductionStatus(AtomicInteger otherBefore, AtomicInteger otherAfter, AtomicInteger anotherReplaced))  {
            return before.get() == otherBefore.get() && after.get() == otherAfter.get() && replaced.get() == anotherReplaced.get();
        }
        return false;
    }
}
