package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.OffsetDateTime;

public class TemplateOffsetDateTime extends TemplatePrimitive<OffsetDateTime> implements TemplateDateTime {
    public TemplateOffsetDateTime(OffsetDateTime value) {
        super(value);
    }
}
