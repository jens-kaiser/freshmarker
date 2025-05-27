package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class TemplateOffsetDateTime extends TemplatePrimitive<OffsetDateTime> implements TemplateDateTime {
    public TemplateOffsetDateTime(OffsetDateTime value) {
        super(value);
    }
}
