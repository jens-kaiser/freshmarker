package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public interface TemplateRange extends TemplateSequence {
    boolean isLengthLimited();

    boolean isRightUnlimited();

    TemplateObject getLower();

    TemplateObject getUpper();

    TemplateRange slice(int min, ProcessContext context);

    TemplateRange slice(int min, int max, ProcessContext context);
}
