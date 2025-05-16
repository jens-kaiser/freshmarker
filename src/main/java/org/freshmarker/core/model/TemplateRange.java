package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public interface TemplateRange extends TemplateSequence {
    boolean isLengthLimited();

    boolean isRightUnlimited();

    TemplateObject getLower();

    TemplateObject getUpper(ProcessContext context);

    default boolean isEmpty(ProcessContext context) {
        return false;
    }

    TemplateRange slice(int min);

    TemplateRange slice(int min, int max);
}
