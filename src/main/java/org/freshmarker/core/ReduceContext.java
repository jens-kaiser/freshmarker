package org.freshmarker.core;

import org.freshmarker.ReductionStatus;
import org.freshmarker.api.FeatureSet;
import org.freshmarker.core.model.TemplateObject;

public class ReduceContext extends ProcessContext {
    private final ReductionStatus status;
    private final FeatureSet featureSet;

    public ReduceContext(ProcessContext context, ReductionStatus status, FeatureSet featureSet) {
        super(context);
        this.status = status;
        this.featureSet = featureSet;
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

    public FeatureSet getFeatureSet() {
        return featureSet;
    }
}
