package org.freshmarker.core.model;

public interface TemplateRange extends TemplateSequence {
    boolean isLengthLimited();

    boolean isRightUnlimited();

    TemplateObject getLower();

    TemplateObject getUpper();
}
