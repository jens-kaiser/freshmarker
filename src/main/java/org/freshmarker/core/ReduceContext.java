package org.freshmarker.core;

import org.freshmarker.ReductionStatus;

public class ReduceContext extends ProcessContext {
    private final ReductionStatus status;

    public ReduceContext(ProcessContext context, ReductionStatus status) {
        super(context.getEnvironment(), context.builtIns, context.outputs);
        this.status = status;
    }

    public ReductionStatus getStatus() {
        return status;
    }

    @Override
    public void reductionCheck() {
        throw new ProcessException("in reduction not allowed");
    }
}
