package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.List;

public interface TemplateRange extends TemplateSequence {
    @Override
    TemplateObject evaluateToObject(ProcessContext context);

    boolean isLengthLimited();

    boolean isRightUnlimited();

    TemplateObject getLower();

    TemplateObject getUpper();

    @Override
    TemplateObject get(ProcessContext context, int index);

    @Override
    TemplateNumber size(ProcessContext context);

    @Override
    List<Object> getSequence(ProcessContext context);
}
