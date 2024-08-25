package org.freshmarker.core;

import org.freshmarker.ReductionStatus;
import org.freshmarker.core.model.TemplateObject;

public class ReduceContext extends ProcessContext {
    private final ReductionStatus status;

    public ReduceContext(ProcessContext context, ReductionStatus status) {
        super(context.baseEnvironment, context.environment, context.builtIns, context.outputs, context.functions);
        this.status = status;
    }

    public ReductionStatus getStatus() {
        return status;
    }

    @Override
    public boolean reductionCheck(TemplateObject templateObject) {
        if (templateObject.isNull()) {
            throw new ProcessException("in reduction not allowed");
        }
        return true;
    }
}
