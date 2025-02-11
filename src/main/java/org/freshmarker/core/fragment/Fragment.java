package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public interface Fragment {

    void process(ProcessContext context);

    default Fragment reduce(ReduceContext context) {
        return this;
    }

    default int getSize() {
        return 1;
    }

    <R> R accept(TemplateVisitor<R> visitor);
}
