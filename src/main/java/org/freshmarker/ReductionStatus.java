package org.freshmarker;

import org.freshmarker.core.model.TemplateObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record ReductionStatus(AtomicInteger before, AtomicInteger after, AtomicInteger replaced, AtomicInteger expression, List<TemplateObject> expressions) {
    public ReductionStatus() {
        this(new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new ArrayList<>());
    }

    public ReductionStatus(int before, int after, int replaced) {
        this(before, after, replaced, -1);
    }

    public ReductionStatus(int before, int after, int replaced, int expression) {
        this(new AtomicInteger(before), new AtomicInteger(after), new AtomicInteger(replaced), new AtomicInteger(expression), new ArrayList<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReductionStatus(AtomicInteger otherBefore, AtomicInteger otherAfter, AtomicInteger anotherReplaced, AtomicInteger anotherExpression, List<TemplateObject> anotherExpressions))  {
            if (expression.get() == -1) {
                return before.get() == otherBefore.get() && after.get() == otherAfter.get() && replaced.get() == anotherReplaced.get();
            }
            return expression.get() == anotherExpression.get();
        }
        return false;
    }
}
